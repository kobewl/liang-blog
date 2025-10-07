package com.liang.blog.dto.todo;

import com.liang.blog.entity.enums.TodoScope;

public class TodoCategorySummaryResponse {

    private final Long id;
    private final String name;
    private final TodoScope scope;
    private final long entryCount;

    public TodoCategorySummaryResponse(Long id, String name, TodoScope scope, long entryCount) {
        this.id = id;
        this.name = name;
        this.scope = scope;
        this.entryCount = entryCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TodoScope getScope() {
        return scope;
    }

    public long getEntryCount() {
        return entryCount;
    }
}
