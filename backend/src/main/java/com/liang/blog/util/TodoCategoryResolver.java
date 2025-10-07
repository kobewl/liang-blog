package com.liang.blog.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liang.blog.entity.TodoCategory;
import com.liang.blog.entity.enums.TodoScope;
import com.liang.blog.mapper.TodoCategoryMapper;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TodoCategoryResolver {

    private final TodoCategoryMapper todoCategoryMapper;
    private final TimeProvider timeProvider;

    public TodoCategoryResolver(TodoCategoryMapper todoCategoryMapper, TimeProvider timeProvider) {
        this.todoCategoryMapper = todoCategoryMapper;
        this.timeProvider = timeProvider;
    }

    public TodoCategory resolveOrCreate(Long userId, TodoScope scope, String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        String normalizedName = categoryName.trim();
        TodoCategory existing = findExisting(userId, scope, normalizedName);
        if (existing != null) {
            return existing;
        }
        LocalDateTime now = timeProvider.now();
        TodoCategory category = new TodoCategory();
        category.setUserId(userId);
        category.setScope(scope.name());
        category.setName(normalizedName);
        category.setSortOrder(0);
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        todoCategoryMapper.insert(category);
        return category;
    }

    public TodoCategory findExisting(Long userId, TodoScope scope, String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return null;
        }
        LambdaQueryWrapper<TodoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TodoCategory::getUserId, userId)
                .eq(TodoCategory::getScope, scope.name())
                .eq(TodoCategory::getName, categoryName.trim())
                .last("LIMIT 1");
        return todoCategoryMapper.selectOne(wrapper);
    }
}
