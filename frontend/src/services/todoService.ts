import apiClient from "./apiClient";

export type TodoScope = "daily" | "weekly" | "monthly";

export interface TodoBoard {
  id: number;
  scope: "DAILY" | "WEEKLY" | "MONTHLY";
  title: string;
  contentMarkdown: string;
  contentHtml: string;
  priority: "LOW" | "MEDIUM" | "HIGH";
  status: "PENDING" | "IN_PROGRESS" | "DONE" | "ARCHIVED";
  updatedAt: string;
}

export interface TodoBoardPayload {
  title?: string;
  contentMarkdown: string;
}

export interface TodoCategorySummary {
  id: number | null;
  name: string;
  scope: TodoBoard["scope"];
  entryCount: number;
}

export interface TodoEntry {
  id: number;
  scope: TodoBoard["scope"];
  title: string;
  contentMarkdown: string;
  contentHtml: string;
  priority: TodoBoard["priority"];
  status: TodoBoard["status"];
  categoryId: number | null;
  categoryName: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface TodoEntryPayload {
  title: string;
  contentMarkdown: string;
  categoryName?: string;
}

export const fetchTodoBoard = async (scope: TodoScope): Promise<TodoBoard> => {
  const { data } = await apiClient.get<TodoBoard>(`/todos/${scope}`);
  return data;
};

export const saveTodoBoard = async (
  scope: TodoScope,
  payload: TodoBoardPayload
): Promise<TodoBoard> => {
  const { data } = await apiClient.put<TodoBoard>(`/todos/${scope}`, payload);
  return data;
};

export const fetchTodoCategories = async (scope: TodoScope): Promise<TodoCategorySummary[]> => {
  const { data } = await apiClient.get<TodoCategorySummary[]>(`/todos/${scope}/categories`);
  return data;
};

export const fetchTodoEntries = async (
  scope: TodoScope,
  categoryId?: number | null
): Promise<TodoEntry[]> => {
  const params = categoryId ? { categoryId } : undefined;
  const { data } = await apiClient.get<TodoEntry[]>(`/todos/${scope}/entries`, { params });
  return data;
};

export const createTodoEntry = async (
  scope: TodoScope,
  payload: TodoEntryPayload
): Promise<TodoEntry> => {
  const { data } = await apiClient.post<TodoEntry>(`/todos/${scope}/entries`, payload);
  return data;
};

export const updateTodoEntry = async (
  scope: TodoScope,
  entryId: number,
  payload: TodoEntryPayload
): Promise<TodoEntry> => {
  const { data } = await apiClient.put<TodoEntry>(`/todos/${scope}/entries/${entryId}`, payload);
  return data;
};
