package com.liang.blog.dto.article;

import com.liang.blog.entity.enums.ArticleStatus;
import java.time.LocalDateTime;

public class ArticleDetailResponse {

    private final Long id;
    private final String slug;
    private final String title;
    private final String summary;
    private final String contentMarkdown;
    private final String contentHtml;
    private final String authorName;
    private final ArticleStatus status;
    private final LocalDateTime publishedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ArticleDetailResponse(
            Long id,
            String slug,
            String title,
            String summary,
            String contentMarkdown,
            String contentHtml,
            String authorName,
            ArticleStatus status,
            LocalDateTime publishedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.contentMarkdown = contentMarkdown;
        this.contentHtml = contentHtml;
        this.authorName = authorName;
        this.status = status;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public String getAuthorName() {
        return authorName;
    }

    public ArticleStatus getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
