package com.liang.blog.dto.todo;

import com.liang.blog.entity.enums.TodoPriority;
import com.liang.blog.entity.enums.TodoScope;
import com.liang.blog.entity.enums.TodoStatus;
import java.time.LocalDateTime;

public class TodoBoardResponse {

    private final Long id;
    private final TodoScope scope;
    private final String title;
    private final String contentMarkdown;
    private final String contentHtml;
    private final TodoPriority priority;
    private final TodoStatus status;
    private final LocalDateTime updatedAt;

    public TodoBoardResponse(
            Long id,
            TodoScope scope,
            String title,
            String contentMarkdown,
            String contentHtml,
            TodoPriority priority,
            TodoStatus status,
            LocalDateTime updatedAt) {
        this.id = id;
        this.scope = scope;
        this.title = title;
        this.contentMarkdown = contentMarkdown;
        this.contentHtml = contentHtml;
        this.priority = priority;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public TodoScope getScope() {
        return scope;
    }

    public String getTitle() {
        return title;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
