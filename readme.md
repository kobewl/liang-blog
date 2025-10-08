# 梁博客（Liang Blog）项目说明

## 项目简介
梁博客是一个集内容创作与效率管理于一体的全栈项目，后端基于 Spring Boot 3 + MySQL，前端采用 Vue 3 + Vite。系统目标是在保持界面简洁的同时，为作者提供文章撰写发布、任务管理（每日/每周/每月）以及 AI 辅助能力。

## 技术栈
- **后端：** Java 21、Spring Boot 3、MyBatis Plus、Jakarta Validation、Spring AI（预留）
- **前端：** Vue 3（组合式 API）、TypeScript、Vite、Tailwind CSS
- **数据库：** MySQL 8（默认通过 docker compose 提供 `liang_blog_db` 服务）

## 目录结构
- `backend/`：Spring Boot 工程
  - `controller/`：REST 控制器（文章、Todo、认证等）
  - `service/`：业务服务（文章、Todo、认证、系统用户提供器）
  - `dto/`：请求/响应对象，按模块划分子包（`article`、`todo`、`user`）
  - `entity/`：MyBatis Plus 实体及枚举
  - `mapper/`：MyBatis Plus Mapper 接口
  - `config/`：`MyBatisPlusConfig`、`WebMvcConfig` 等全局配置
  - `util/`：通用工具（如 `MarkdownRenderer`）
- `frontend/`：Vite + Vue 3 前端工程
  - `views/`：页面级组件（首页、文章详情、Todo 面板等）
  - `services/`：统一封装 API 调用（`articleService`、`todoService`）
- `docs/sql/`：数据库初始化脚本
- `需求文档.md`：总体需求说明
- `AGENTS.md`：协作说明与 AI 助手约定

## 本地开发流程
1. **准备数据库**（首次运行）
   ```bash
   docker compose up db
   ```
2. **启动后端**（任选其一）
   ```bash
   # 若已安装 Maven
   (cd backend && mvn spring-boot:run)
   ```
3. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   前端开发服务器默认运行在 <http://localhost:5173>，并通过代理访问后端 `http://localhost:8080`。

### 配置管理
- 全局默认配置在 `backend/src/main/resources/application.yml` 中维护，不含任何敏感值，相关字段通过环境变量占位符（如 `LIANG_BLOG_DB_USERNAME`）读取。
- 项目已默认启用 `local` Profile（`spring.profiles.default=local`），只要存在 `backend/src/main/resources/application-local.yml`，启动后端时会自动读取其中的数据库账号和 AI Key。
- `application-local.yml` 已加入 `.gitignore`，不会提交到仓库；如需切换到其他环境，可在启动命令中显式设置 `SPRING_PROFILES_ACTIVE=prod` 等覆盖默认值。

## 接口约定
- 后端统一返回 `ApiResponse` 结构：`code`（0 表示成功）、`message`（友好提示）、`data`（业务数据）。
- 需要登录的接口须在请求头携带 `Authorization: Bearer <token>`，该 token 由 `/api/auth/login` 或 `/api/auth/register` 返回。
- JWT 默认 2 小时过期，token 过期后调用接口会返回 401，需要重新登录获取新 token。
- Todo 分类统计会额外返回 `全部`（`id=null`）与 `无标签`（`id=0`）两项，方便前端直接渲染筛选入口。

## 已实现功能与接口

### 文章（Articles）
| 场景 | 方法 | 路径 | 请求体 | 响应内容 |
| ---- | ---- | ---- | ------ | -------- |
| 获取已发布文章列表 | GET | `/api/articles` | 无 | `ArticleSummary[]`，字段包括 `id`、`slug`、`title`、`summary`、`authorName`、`publishedAt` |
| 获取单篇已发布文章 | GET | `/api/articles/{slug}` | 无 | `ArticleDetail`，包含 `ArticleSummary` 字段以及 `contentMarkdown`、`contentHtml`、`status`、`createdAt`、`updatedAt` |
| （管理端）创建文章 | POST | `/api/admin/articles` | `ArticleCreateRequest`：`title`、`summary?`、`contentMarkdown`、`status?`、`authorName?` | 返回新建 `ArticleDetail` |
| （管理端）更新文章 | PUT | `/api/admin/articles/{id}` | `ArticleUpdateRequest`：`title`、`summary?`、`contentMarkdown`、`status?`、`authorName?` | 返回更新后的 `ArticleDetail` |
| （管理端）查看所有文章 | GET | `/api/admin/articles` | 无 | `ArticleDetail[]` |
| （管理端）查看单篇 | GET | `/api/admin/articles/{id}` | 无 | `ArticleDetail` |

> **说明：**
> - `status` 枚举：`DRAFT`、`PUBLISHED`、`ARCHIVED`
> - 已发布文章自动设置 `publishedAt` 时间，未发布则为 `null`
> - Markdown 原文通过后端服务转换为 HTML 存储，前端可直接渲染

### Todo 面板（全屏任务记录）
| 场景 | 方法 | 路径 | 请求体 | 响应内容 |
| ---- | ---- | ---- | ------ | -------- |
| 获取任务标签统计 | GET | `/api/todos/{scope}/categories` | 无 | `TodoCategorySummary[]`：`id?`、`name`、`scope`、`entryCount` |
| 获取任务历史列表 | GET | `/api/todos/{scope}/entries` | `categoryId?`（可选，留空表示全部） | `TodoEntry[]`：包含 `title`、`contentMarkdown`、`contentHtml`、`categoryId?`、`categoryName?`、`createdAt`、`updatedAt` |
| 创建任务记录 | POST | `/api/todos/{scope}/entries` | `TodoEntryUpsertRequest`：`title`、`contentMarkdown`、`categoryName?` | 返回新建 `TodoEntry` |
| 更新任务记录 | PUT | `/api/todos/{scope}/entries/{id}` | `TodoEntryUpsertRequest` | 返回更新后的 `TodoEntry` |

> **使用说明：**
> - `scope` 支持 `daily` / `weekly` / `monthly`，后端根据 JWT 中的用户 ID 自动隔离数据。
> - 分类统计接口会返回“全部”“无标签”以及所有自定义标签；创建或更新任务时填写新的 `categoryName` 会自动建类。
> - Markdown 内容会在保存时转换为 HTML 存入 `todo_items.content_html`，方便前端直接渲染预览。
> - 历史记录默认以创建时间倒序返回，任务状态默认为 `PENDING`，后续可扩展完成/归档能力。
> - 所有表均启用逻辑删除（`is_deleted` 字段），MyBatis Plus 自动拼接删除条件。
> - 历史记录中的复选框支持直接点击切换完成状态，前端会自动调用任务更新接口并同步 Markdown 文本。

#### 旧版单任务板接口（兼容保留）
| 场景 | 方法 | 路径 | 请求体 | 响应内容 |
| ---- | ---- | ---- | ------ | -------- |
| 获取指定范围任务板 | GET | `/api/todos/{scope}` | 无 | `TodoBoard` |
| 保存/更新任务板 | PUT | `/api/todos/{scope}` | `TodoBoardUpsertRequest` | 返回最新 `TodoBoard` |

**前端页面亮点：**
- 全屏双栏布局：左侧编辑器（标题、标签、Markdown、快捷按钮、实时预览），右侧为历史任务列表。
- 支持非 Markdown 用户：点击标签、输入框或快捷按钮即可自动插入 `- [ ]` 待办语句。
- 分类筛选：右侧标签统计展示 `全部 / 无标签 / 自定义标签`，可一键筛选历史记录。
- 历史面板展示已勾选/未勾选项摘要，点击即可回填至编辑器继续修改。
- 保存操作会刷新标签统计与历史列表，并提示“已保存至历史任务”。

### 认证 & 用户
| 场景 | 方法 | 路径 | 请求体 | 响应内容 |
| ---- | ---- | ---- | ------ | -------- |
| 用户注册 | POST | `/api/auth/register` | `RegisterRequest`：`account`、`email`、`password`、`displayName` | `AuthResponse`：`token`、`user` |
| 用户登录 | POST | `/api/auth/login` | `LoginRequest`：`accountOrEmail`、`password` | `AuthResponse` |
| 获取个人信息 | GET | `/api/auth/me` | 需携带 `Authorization: Bearer <token>` | `UserProfileResponse` |
| 更新个人信息 | PUT | `/api/auth/me` | `UserProfileUpdateRequest`：`displayName?`、`avatarUrl?`、`bio?` | 更新后的 `UserProfileResponse` |

> - 注册/登录都会返回 `AuthResponse`，其中 `token` 字段可直接放入后续请求的 `Authorization` 头部。
> - 密码使用 `BCrypt` 哈希存储；Token 由自定义 JWT 组件签发，无需引入 Spring Security。
> - 所有用户数据默认 `status=ACTIVE`，逻辑删除通过 `is_deleted` 字段控制。

### AI 对话
| 场景 | 方法 | 路径 | 请求体 | 响应内容 |
| ---- | ---- | ---- | ------ | -------- |
| 获取 AI 流式回复 | POST | `/api/ai/chat` | `AiChatRequest`：`userMessage` | 返回 `text/event-stream`，每条事件的 `data:` 行即为模型输出文本片段 |

> - 需要在请求头声明 `Accept: text/event-stream`，否则客户端可能看不到逐条推送。
> - 调用示例（curl）：`curl -X POST http://127.0.0.1:8080/api/ai/chat -H "Content-Type: application/json" -H "Accept: text/event-stream" -d '{"userMessage":"你好"}'`。
> - 如果要在浏览器中消费，可使用 `EventSource` 或 `fetch` + `ReadableStream` 监听每段文本。

### 数据库设计更新
- `docs/sql/schema.sql` 重新整理所有表结构，补充 `account`、`avatar_url` 等用户信息字段，并对文章、评论、Todo 等表引入 `is_deleted` 逻辑删除位。
- 全局启用 MyBatis Plus 逻辑删除（`is_deleted = 1`），配合实体类 `@TableLogic` 注解即可软删数据。
- 关系表如 `article_tags` 也提供时间戳与逻辑删除字段，方便日后审计与恢复。

## 测试与质量保证
- 后端建议执行 `mvn test`（可通过 `-Dmaven.repo.local` 指定本地仓库路径）。若在离线/受限网络下需预先准备依赖。
- 前端执行 `npm run lint` 保持代码风格统一。

## 下一步规划
1. **任务流程加强**：补充完成状态切换、批量归档、任务提醒，完善 `TodoStatus` 的使用场景。
2. **AI 接入**：接入 Spring AI，提供文章摘要与 Todo 拆解建议，并记录调用日志。
3. **认证强化**：补充注销/刷新 Token、异常登录锁定与密码找回；完善审计日志。
4. **前端体验**：补充暗色模式、响应式导航，以及任务与文章的关联跳转。

## 项目反思与改进建议
- 认证流程已具备注册/登录与 JWT 校验，但仍缺少刷新 token、注销和异常登录保护，后续需要补齐安全闭环。
- Todo 历史与任务板共用一张表，当前以状态加时间区分，有必要在未来引入显式标记或独立表，避免互相干扰。
- Markdown 编辑仍基于原生 `textarea`，可以评估引入 tiptap、v-md-editor 等组件以提升编辑体验与快捷键支持。
- 分类目前自动按需创建，后续可增加标签管理接口（排序、重命名、颜色），并提供批量归档能力。
- 受限网络下 Maven 依赖拉取可能失败，建议准备离线仓库或在 CI/CD 中预热缓存。
- AI 对话接口改为标准的 SSE 流式输出后，现有统一 `ApiResponse` 包装被绕过；如需保持统一返回风格，可评估新增非流式回退接口或封装前端的 SSE 适配层。
