package com.liang.blog.util;

import com.liang.blog.dto.todo.TodoBoardResponse;
import com.liang.blog.dto.todo.TodoCategorySummaryResponse;
import com.liang.blog.dto.todo.TodoEntryResponse;
import com.liang.blog.entity.TodoCategory;
import com.liang.blog.entity.TodoItem;
import com.liang.blog.entity.enums.TodoPriority;
import com.liang.blog.entity.enums.TodoScope;
import com.liang.blog.entity.enums.TodoStatus;

public final class TodoResponseMapper {

    private TodoResponseMapper() {}

    public static TodoEntryResponse toEntry(TodoItem item, TodoCategory category) {
        if (item == null) {
            return null;
        }
        TodoScope scope = EnumParser.parseOrDefault(TodoScope.class, item.getScope(), TodoScope.DAILY);
        TodoPriority priority =
                EnumParser.parseOrDefault(TodoPriority.class, item.getPriority(), TodoPriority.MEDIUM);
        TodoStatus status = EnumParser.parseOrDefault(TodoStatus.class, item.getStatus(), TodoStatus.PENDING);
        Long categoryId = category != null ? category.getId() : null;
        String categoryName = category != null ? category.getName() : null;
        return new TodoEntryResponse(
                item.getId(),
                scope,
                item.getTitle(),
                item.getContentMarkdown(),
                item.getContentHtml(),
                priority,
                status,
                categoryId,
                categoryName,
                item.getCreatedAt(),
                item.getUpdatedAt());
    }

    public static TodoBoardResponse toBoard(TodoItem item) {
        if (item == null) {
            return null;
        }
        TodoScope scope = EnumParser.parseOrDefault(TodoScope.class, item.getScope(), TodoScope.DAILY);
        TodoPriority priority =
                EnumParser.parseOrDefault(TodoPriority.class, item.getPriority(), TodoPriority.MEDIUM);
        TodoStatus status = EnumParser.parseOrDefault(TodoStatus.class, item.getStatus(), TodoStatus.PENDING);
        return new TodoBoardResponse(
                item.getId(),
                scope,
                item.getTitle(),
                item.getContentMarkdown(),
                item.getContentHtml(),
                priority,
                status,
                item.getUpdatedAt());
    }

    public static TodoCategorySummaryResponse toCategorySummary(
            TodoCategory category, long entryCount) {
        if (category == null) {
            return null;
        }
        TodoScope scope = EnumParser.parseOrDefault(TodoScope.class, category.getScope(), TodoScope.DAILY);
        return new TodoCategorySummaryResponse(category.getId(), category.getName(), scope, entryCount);
    }
}
