-- Liang Blog schema (MySQL 8)
-- Updated for MyBatis Plus integration and logical delete support.

-- Users
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    account         VARCHAR(100)    NOT NULL COMMENT '用户账号',
    email           VARCHAR(255) COMMENT '邮箱地址',
    password_hash   VARCHAR(255)    NOT NULL COMMENT '密码哈希',
    display_name    VARCHAR(100)    NOT NULL COMMENT '用户昵称',
    avatar_url      VARCHAR(512) COMMENT '头像图片 URL',
    status          ENUM('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态',
    bio             TEXT COMMENT '个人简介',
    last_login_at   DATETIME(6) COMMENT '最近登录时间',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除(0=正常,1=删除)',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_account (account),
    UNIQUE KEY uk_users_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Articles
CREATE TABLE IF NOT EXISTS articles (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    author_id           BIGINT UNSIGNED COMMENT '作者ID',
    author_name         VARCHAR(100) COMMENT '作者名称',
    slug                VARCHAR(191)   NOT NULL COMMENT '文章别名 Slug',
    title               VARCHAR(255)   NOT NULL COMMENT '文章标题',
    summary             TEXT COMMENT '文章摘要',
    cover_image_url     VARCHAR(512) COMMENT '封面图 URL',
    content_markdown    LONGTEXT       NOT NULL COMMENT 'Markdown 正文',
    content_html        LONGTEXT COMMENT '渲染后的 HTML 正文',
    status              ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT' COMMENT '文章状态',
    published_at        DATETIME(6) COMMENT '发布时间',
    is_deleted          TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_articles_slug (slug),
    KEY idx_articles_status_published_at (status, published_at),
    CONSTRAINT fk_articles_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Tags
CREATE TABLE IF NOT EXISTS tags (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name            VARCHAR(100)    NOT NULL COMMENT '标签名称',
    description     VARCHAR(255) COMMENT '标签描述',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tags_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Article & Tag relation
CREATE TABLE IF NOT EXISTS article_tags (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id      BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    tag_id          BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    CONSTRAINT fk_article_tags_article FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    CONSTRAINT fk_article_tags_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Comments
CREATE TABLE IF NOT EXISTS comments (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    article_id          BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    user_id             BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    parent_id           BIGINT UNSIGNED COMMENT '父评论ID',
    content_markdown    LONGTEXT       NOT NULL COMMENT '评论 Markdown',
    content_html        LONGTEXT COMMENT '评论 HTML',
    status              ENUM('VISIBLE', 'HIDDEN', 'PENDING') NOT NULL DEFAULT 'VISIBLE' COMMENT '状态',
    is_deleted          TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_comments_article (article_id),
    KEY idx_comments_parent (parent_id),
    CONSTRAINT fk_comments_article FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Todo categories
CREATE TABLE IF NOT EXISTS todo_categories (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
    name            VARCHAR(100)    NOT NULL COMMENT '分类名称',
    scope           ENUM('DAILY', 'WEEKLY', 'MONTHLY') NOT NULL COMMENT '适用范围',
    sort_order      INT             NOT NULL DEFAULT 0 COMMENT '排序值',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_todo_categories_name (user_id, name, scope),
    CONSTRAINT fk_todo_categories_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Todo items
CREATE TABLE IF NOT EXISTS todo_items (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id             BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
    category_id         BIGINT UNSIGNED COMMENT '分类ID',
    related_article_id  BIGINT UNSIGNED COMMENT '关联文章ID',
    scope               ENUM('DAILY', 'WEEKLY', 'MONTHLY') NOT NULL COMMENT '适用范围',
    title               VARCHAR(255)   NOT NULL COMMENT '任务标题',
    content_markdown    LONGTEXT       NOT NULL COMMENT 'Markdown 内容',
    content_html        LONGTEXT COMMENT 'HTML 内容',
    priority            ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级',
    status              ENUM('PENDING', 'IN_PROGRESS', 'DONE', 'ARCHIVED') NOT NULL DEFAULT 'PENDING' COMMENT '任务状态',
    start_date          DATE COMMENT '开始日期',
    due_date            DATE COMMENT '截止日期',
    completed_at        DATETIME(6) COMMENT '完成时间',
    sort_order          INT            NOT NULL DEFAULT 0 COMMENT '排序值',
    is_deleted          TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at          DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_todo_items_scope (scope),
    KEY idx_todo_items_due_date (due_date),
    CONSTRAINT fk_todo_items_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_todo_items_category FOREIGN KEY (category_id) REFERENCES todo_categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_todo_items_article FOREIGN KEY (related_article_id) REFERENCES articles (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- Todo links
CREATE TABLE IF NOT EXISTS todo_links (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    todo_item_id    BIGINT UNSIGNED NOT NULL COMMENT '任务ID',
    link_type       ENUM('URL', 'RESOURCE', 'ARTICLE') NOT NULL COMMENT '链接类型',
    target_value    VARCHAR(512)    NOT NULL COMMENT '链接内容',
    is_deleted      TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_todo_links_item (todo_item_id),
    CONSTRAINT fk_todo_links_item FOREIGN KEY (todo_item_id) REFERENCES todo_items (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- AI logs
CREATE TABLE IF NOT EXISTS ai_logs (
    id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id           BIGINT UNSIGNED COMMENT '用户ID',
    request_type      VARCHAR(100)   NOT NULL COMMENT '请求类型',
    model             VARCHAR(100) COMMENT '模型名称',
    prompt_excerpt    TEXT COMMENT '提示词摘要',
    response_excerpt  TEXT COMMENT '响应摘要',
    total_tokens      INT COMMENT '消耗 Token',
    latency_ms        INT COMMENT '耗时(ms)',
    status            ENUM('SUCCESS', 'FAILED') NOT NULL DEFAULT 'SUCCESS' COMMENT '请求状态',
    is_deleted        TINYINT(1)     NOT NULL DEFAULT 0 COMMENT '是否删除',
    created_at        DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    updated_at        DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_ai_logs_type (request_type),
    CONSTRAINT fk_ai_logs_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
