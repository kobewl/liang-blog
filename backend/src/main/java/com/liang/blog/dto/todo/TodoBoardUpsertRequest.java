package com.liang.blog.dto.todo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TodoBoardUpsertRequest {

    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @NotNull(message = "任务内容不能为空")
    private String contentMarkdown;

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
}
