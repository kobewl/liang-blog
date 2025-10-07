package com.liang.blog.dto.article;

import java.time.LocalDateTime;

public class ArticleSummaryResponse {

    private final Long id;
    private final String slug;
    private final String title;
    private final String summary;
    private final String authorName;
    private final LocalDateTime publishedAt;

    public ArticleSummaryResponse(Long id, String slug, String title, String summary, String authorName, LocalDateTime publishedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.authorName = authorName;
        this.publishedAt = publishedAt;
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

    public String getAuthorName() {
        return authorName;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
}
