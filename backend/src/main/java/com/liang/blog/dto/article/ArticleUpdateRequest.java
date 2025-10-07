package com.liang.blog.dto.article;

import com.liang.blog.entity.enums.ArticleStatus;
import jakarta.validation.constraints.NotBlank;

public class ArticleUpdateRequest {

    @NotBlank
    private String title;

    private String summary;

    @NotBlank
    private String contentMarkdown;

    private ArticleStatus status = ArticleStatus.DRAFT;

    private String authorName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public void setContentMarkdown(String contentMarkdown) {
        this.contentMarkdown = contentMarkdown;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
