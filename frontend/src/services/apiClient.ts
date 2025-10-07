import axios from "axios";

const TOKEN_KEY = "liang_blog_token";

export class ApiError extends Error {
  status?: number;
  data?: unknown;
}

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api"
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => {
    const body = response.data as {
      code?: number;
      message?: string;
      data?: unknown;
    } | undefined;

    if (body && typeof body === "object" && "code" in body) {
      if (body.code === 0) {
        response.data = body.data as typeof response.data;
        return response;
      }

      const apiError = new ApiError(body.message || "请求失败");
      apiError.status = body.code;
      apiError.data = body;
      return Promise.reject(apiError);
    }

    return response;
  },
  (error) => {
    const message = error?.response?.data?.message || error.message || "请求失败";
    const apiError = new ApiError(message);
    apiError.status = error?.response?.status;
    apiError.data = error?.response?.data;
    return Promise.reject(apiError);
  }
);

export default apiClient;
