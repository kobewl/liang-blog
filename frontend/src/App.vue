<template>
  <div class="min-h-screen flex flex-col">
    <header class="border-b border-slate-200 bg-white">
      <div class="flex items-center justify-between px-6 py-4 mx-auto max-w-5xl">
        <RouterLink to="/" class="text-xl font-semibold text-brand">Liang Blog</RouterLink>
        <nav class="flex items-center gap-4 text-sm text-slate-600">
          <RouterLink class="hover:text-brand" to="/">文章</RouterLink>
          <RouterLink class="hover:text-brand" to="/todo">TodoList</RouterLink>
          <RouterLink class="hover:text-brand" to="/admin/articles/new">写文章</RouterLink>
          <template v-if="auth.token">
            <RouterLink class="flex items-center gap-2 hover:text-brand" to="/profile">
              <img
                :src="avatarUrl"
                alt="avatar"
                class="h-8 w-8 rounded-full border border-slate-200 object-cover"
              />
              <span class="font-medium text-slate-700">{{ auth.user?.displayName || auth.user?.account }}</span>
            </RouterLink>
            <button class="text-xs text-slate-500 hover:text-red-500" @click="logout">退出</button>
          </template>
          <template v-else>
            <RouterLink class="hover:text-brand" to="/signin">登录</RouterLink>
            <RouterLink class="hover:text-brand" to="/register">注册</RouterLink>
          </template>
        </nav>
      </div>
    </header>

    <main class="flex-1 bg-slate-100/60">
      <RouterView />
    </main>

    <footer class="px-6 py-8 text-sm text-center text-slate-500 bg-white border-t border-slate-200">
      © {{ new Date().getFullYear() }} Liang Blog. 借助 Spring AI 提升创作与效率。
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, RouterView } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();

const avatarUrl = computed(() => {
  if (!auth.user) {
    return "https://api.dicebear.com/7.x/identicon/svg?seed=liang";
  }
  return auth.user.avatarUrl || `https://api.dicebear.com/7.x/identicon/svg?seed=${auth.user.account}`;
});

const logout = () => {
  auth.logout();
};
</script>
