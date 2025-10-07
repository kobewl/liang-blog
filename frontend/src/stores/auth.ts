import { defineStore } from "pinia";
import { register as registerApi, login as loginApi, fetchProfile, updateProfile as updateProfileApi, type AuthResponse, type LoginPayload, type ProfileUpdatePayload, type RegisterPayload, type UserProfile } from "@/services/authService";

interface AuthState {
  token: string | null;
  user: UserProfile | null;
  loading: boolean;
  error: string | null;
}

const TOKEN_KEY = "liang_blog_token";

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    token: null,
    user: null,
    loading: false,
    error: null
  }),
  actions: {
    initialize() {
      const token = localStorage.getItem(TOKEN_KEY);
      if (token) {
        this.token = token;
        this.loadProfile();
      }
    },
    setAuth(response: AuthResponse) {
      this.token = response.token;
      this.user = response.user;
      localStorage.setItem(TOKEN_KEY, response.token);
    },
    clearAuth() {
      this.token = null;
      this.user = null;
      localStorage.removeItem(TOKEN_KEY);
    },
    async register(payload: RegisterPayload) {
      this.loading = true;
      this.error = null;
      try {
        const response = await registerApi(payload);
        this.setAuth(response);
      } catch (err) {
        this.error = err instanceof Error ? err.message : "注册失败";
        throw err;
      } finally {
        this.loading = false;
      }
    },
    async login(payload: LoginPayload) {
      this.loading = true;
      this.error = null;
      try {
        const response = await loginApi(payload);
        this.setAuth(response);
      } catch (err) {
        this.error = err instanceof Error ? err.message : "登录失败";
        throw err;
      } finally {
        this.loading = false;
      }
    },
    async loadProfile() {
      if (!this.token) {
        return;
      }
      try {
        this.user = await fetchProfile();
      } catch (err) {
        this.clearAuth();
      }
    },
    async updateProfile(payload: ProfileUpdatePayload) {
      if (!this.token) {
        throw new Error("未登录");
      }
      const profile = await updateProfileApi(payload);
      this.user = profile;
      return profile;
    },
    logout() {
      this.clearAuth();
    }
  }
});
