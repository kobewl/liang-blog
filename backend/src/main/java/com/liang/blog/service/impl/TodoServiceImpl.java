package com.liang.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liang.blog.dto.todo.TodoBoardResponse;
import com.liang.blog.dto.todo.TodoBoardUpsertRequest;
import com.liang.blog.dto.todo.TodoCategorySummaryResponse;
import com.liang.blog.dto.todo.TodoEntryResponse;
import com.liang.blog.dto.todo.TodoEntryUpsertRequest;
import com.liang.blog.entity.TodoCategory;
import com.liang.blog.entity.TodoItem;
import com.liang.blog.entity.enums.TodoPriority;
import com.liang.blog.entity.enums.TodoScope;
import com.liang.blog.entity.enums.TodoStatus;
import com.liang.blog.mapper.TodoCategoryMapper;
import com.liang.blog.mapper.TodoItemMapper;
import com.liang.blog.service.TodoService;
import com.liang.blog.util.MarkdownRenderer;
import com.liang.blog.util.TimeProvider;
import com.liang.blog.util.TodoCategoryResolver;
import com.liang.blog.util.TodoResponseMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoItemMapper todoItemMapper;
    private final TodoCategoryMapper todoCategoryMapper;
    private final TodoCategoryResolver todoCategoryResolver;
    private final MarkdownRenderer markdownRenderer;
    private final TimeProvider timeProvider;

    public TodoServiceImpl(
            TodoItemMapper todoItemMapper,
            TodoCategoryMapper todoCategoryMapper,
            TodoCategoryResolver todoCategoryResolver,
            MarkdownRenderer markdownRenderer,
            TimeProvider timeProvider) {
        this.todoItemMapper = todoItemMapper;
        this.todoCategoryMapper = todoCategoryMapper;
        this.todoCategoryResolver = todoCategoryResolver;
        this.markdownRenderer = markdownRenderer;
        this.timeProvider = timeProvider;
    }

    @Override
    public List<TodoCategorySummaryResponse> listCategorySummary(Long userId, TodoScope scope) {
        List<TodoItemMapper.CategoryEntryCount> counts =
                todoItemMapper.countByCategory(userId, scope.name());
        Map<Long, Long> countMap = new HashMap<>();
        long totalCount = 0;
        long untaggedCount = 0;
        for (TodoItemMapper.CategoryEntryCount item : counts) {
            long value = item.getEntryCount() == null ? 0 : item.getEntryCount();
            Long categoryId = item.getCategoryId();
            totalCount += value;
            if (categoryId == null) {
                untaggedCount = value;
            } else {
                countMap.put(categoryId, value);
            }
        }

        LambdaQueryWrapper<TodoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TodoCategory::getUserId, userId)
                .eq(TodoCategory::getScope, scope.name())
                .orderByAsc(TodoCategory::getSortOrder)
                .orderByAsc(TodoCategory::getId);
        List<TodoCategory> categories = todoCategoryMapper.selectList(wrapper);

        List<TodoCategorySummaryResponse> responses = new ArrayList<>();
        responses.add(new TodoCategorySummaryResponse(null, "全部", scope, totalCount));
        responses.add(new TodoCategorySummaryResponse(0L, "无标签", scope, untaggedCount));
        for (TodoCategory category : categories) {
            long count = countMap.getOrDefault(category.getId(), 0L);
            responses.add(TodoResponseMapper.toCategorySummary(category, count));
        }
        return responses;
    }

    @Override
    public List<TodoEntryResponse> listEntries(Long userId, TodoScope scope, Long categoryId) {
        LambdaQueryWrapper<TodoItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TodoItem::getUserId, userId)
                .eq(TodoItem::getScope, scope.name())
                .orderByDesc(TodoItem::getCreatedAt);
        if (categoryId != null) {
            if (categoryId == 0L) {
                wrapper.isNull(TodoItem::getCategoryId);
            } else {
                wrapper.eq(TodoItem::getCategoryId, categoryId);
            }
        }
        List<TodoItem> items = todoItemMapper.selectList(wrapper);
        Set<Long> categoryIds = items.stream()
                .map(TodoItem::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, TodoCategory> categoryMap = categoryIds.isEmpty()
                ? Map.of()
                : todoCategoryMapper.selectBatchIds(categoryIds).stream()
                        .collect(Collectors.toMap(TodoCategory::getId, c -> c));
        List<TodoEntryResponse> responses = new ArrayList<>(items.size());
        for (TodoItem item : items) {
            TodoCategory category = categoryMap.get(item.getCategoryId());
            responses.add(TodoResponseMapper.toEntry(item, category));
        }
        return responses;
    }

    @Override
    @Transactional
    public TodoEntryResponse createEntry(Long userId, TodoScope scope, TodoEntryUpsertRequest request) {
        TodoCategory category =
                todoCategoryResolver.resolveOrCreate(userId, scope, request.getCategoryName());
        LocalDateTime now = timeProvider.now();
        TodoItem item = new TodoItem();
        item.setUserId(userId);
        item.setScope(scope.name());
        item.setCategoryId(category != null ? category.getId() : null);
        item.setTitle(request.getTitle().trim());
        item.setContentMarkdown(request.getContentMarkdown());
        item.setContentHtml(markdownRenderer.render(request.getContentMarkdown()));
        item.setPriority(TodoPriority.MEDIUM.name());
        item.setStatus(TodoStatus.PENDING.name());
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        todoItemMapper.insert(item);
        return TodoResponseMapper.toEntry(item, category);
    }

    @Override
    @Transactional
    public TodoEntryResponse updateEntry(Long userId, TodoScope scope, Long entryId, TodoEntryUpsertRequest request) {
        TodoItem item = todoItemMapper.selectById(entryId);
        if (item == null || !Objects.equals(item.getUserId(), userId)) {
            throw new IllegalArgumentException("任务不存在");
        }
        if (!scope.name().equals(item.getScope())) {
            throw new IllegalArgumentException("任务范围不一致");
        }
        TodoCategory category = null;
        if (request.getCategoryName() != null) {
            if (StringUtils.hasText(request.getCategoryName())) {
                category = todoCategoryResolver.resolveOrCreate(userId, scope, request.getCategoryName());
                item.setCategoryId(category.getId());
            } else {
                item.setCategoryId(null);
            }
        } else if (item.getCategoryId() != null) {
            category = todoCategoryMapper.selectById(item.getCategoryId());
        }
        item.setTitle(request.getTitle().trim());
        item.setContentMarkdown(request.getContentMarkdown());
        item.setContentHtml(markdownRenderer.render(request.getContentMarkdown()));
        item.setUpdatedAt(timeProvider.now());
        todoItemMapper.updateById(item);
        return TodoResponseMapper.toEntry(item, category);
    }

    @Override
    public TodoBoardResponse getBoard(Long userId, TodoScope scope) {
        TodoItem item = findLatestBoardItem(userId, scope);
        if (item == null) {
            return new TodoBoardResponse(
                    null,
                    scope,
                    "",
                    "",
                    "",
                    TodoPriority.MEDIUM,
                    TodoStatus.PENDING,
                    null);
        }
        return TodoResponseMapper.toBoard(item);
    }

    @Override
    @Transactional
    public TodoBoardResponse saveBoard(Long userId, TodoScope scope, TodoBoardUpsertRequest request) {
        TodoItem current = findLatestBoardItem(userId, scope);
        LocalDateTime now = timeProvider.now();
        if (current == null) {
            TodoItem item = new TodoItem();
            item.setUserId(userId);
            item.setScope(scope.name());
            item.setTitle(resolveBoardTitle(request.getTitle()));
            item.setContentMarkdown(request.getContentMarkdown());
            item.setContentHtml(markdownRenderer.render(request.getContentMarkdown()));
            item.setPriority(TodoPriority.MEDIUM.name());
            item.setStatus(TodoStatus.IN_PROGRESS.name());
            item.setCreatedAt(now);
            item.setUpdatedAt(now);
            todoItemMapper.insert(item);
            return TodoResponseMapper.toBoard(item);
        }
        current.setTitle(resolveBoardTitle(request.getTitle()));
        current.setContentMarkdown(request.getContentMarkdown());
        current.setContentHtml(markdownRenderer.render(request.getContentMarkdown()));
        current.setUpdatedAt(now);
        todoItemMapper.updateById(current);
        return TodoResponseMapper.toBoard(current);
    }

    private TodoItem findLatestBoardItem(Long userId, TodoScope scope) {
        LambdaQueryWrapper<TodoItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TodoItem::getUserId, userId)
                .eq(TodoItem::getScope, scope.name())
                .ne(TodoItem::getStatus, TodoStatus.ARCHIVED.name())
                .orderByDesc(TodoItem::getUpdatedAt)
                .last("LIMIT 1");
        return todoItemMapper.selectOne(wrapper);
    }

    private String resolveBoardTitle(String title) {
        if (StringUtils.hasText(title)) {
            return title.trim();
        }
        return "临时任务板";
    }
}
