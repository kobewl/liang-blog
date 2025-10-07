import apiClient from "./apiClient";

export interface ArticleSummary {
  id: number;
  slug: string;
  title: string;
  summary: string | null;
  authorName: string | null;
  publishedAt: string | null;
}

export interface ArticleDetail extends ArticleSummary {
  contentMarkdown: string;
  contentHtml: string;
  status: "DRAFT" | "PUBLISHED" | "ARCHIVED";
  createdAt: string;
  updatedAt: string;
}

export interface CreateArticlePayload {
  title: string;
  summary?: string;
  contentMarkdown: string;
  status?: ArticleDetail["status"];
  authorName?: string;
}

export const fetchPublishedArticles = async (): Promise<ArticleSummary[]> => {
  const { data } = await apiClient.get<ArticleSummary[]>("/articles");
  return data;
};

export const fetchArticleBySlug = async (slug: string): Promise<ArticleDetail> => {
  const { data } = await apiClient.get<ArticleDetail>(`/articles/${slug}`);
  return data;
};

export const createArticle = async (payload: CreateArticlePayload): Promise<ArticleDetail> => {
  const { data } = await apiClient.post<ArticleDetail>("/admin/articles", payload);
  return data;
};
