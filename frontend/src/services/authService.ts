import apiClient from "./apiClient";

export interface UserProfile {
  id: number;
  account: string;
  email: string;
  displayName: string;
  avatarUrl: string | null;
  bio: string | null;
  status: "ACTIVE" | "SUSPENDED";
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: UserProfile;
}

export interface RegisterPayload {
  account: string;
  email: string;
  password: string;
  displayName: string;
}

export interface LoginPayload {
  accountOrEmail: string;
  password: string;
}

export interface ProfileUpdatePayload {
  displayName?: string;
  avatarUrl?: string;
  bio?: string;
}

export const register = async (payload: RegisterPayload): Promise<AuthResponse> => {
  const { data } = await apiClient.post<AuthResponse>("/auth/register", payload);
  return data;
};

export const login = async (payload: LoginPayload): Promise<AuthResponse> => {
  const { data } = await apiClient.post<AuthResponse>("/auth/login", payload);
  return data;
};

export const fetchProfile = async (): Promise<UserProfile> => {
  const { data } = await apiClient.get<UserProfile>("/auth/me");
  return data;
};

export const updateProfile = async (payload: ProfileUpdatePayload): Promise<UserProfile> => {
  const { data } = await apiClient.put<UserProfile>("/auth/me", payload);
  return data;
};
