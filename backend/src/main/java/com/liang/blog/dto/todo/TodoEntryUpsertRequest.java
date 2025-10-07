package com.liang.blog.dto.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TodoEntryUpsertRequest {

    @NotBlank(message = "任务标题不能为空")
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @NotNull(message = "内容不能为空")
    private String contentMarkdown;

    @Size(max = 100, message = "标签长度不能超过100个字符")
    private String categoryName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public void setContentMarkdown(String contentMarkdown) {
        this.contentMarkdown = contentMarkdown;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
