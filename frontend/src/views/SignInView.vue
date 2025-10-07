<template>
  <section class="min-h-[70vh] flex items-center justify-center px-6 py-10 bg-gradient-to-b from-slate-50 to-white">
    <div class="w-full max-w-md p-8 bg-white border border-slate-200/60 rounded-2xl shadow-sm space-y-6">
      <header class="space-y-2 text-center">
        <h1 class="text-2xl font-semibold text-slate-900">登录 Liang Blog</h1>
        <p class="text-sm text-slate-500">评论文章和同步 TodoList 需要登录。</p>
      </header>
      <form class="space-y-4" @submit.prevent="handleLogin">
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="email">邮箱</label>
          <input
            id="accountOrEmail"
            v-model="form.accountOrEmail"
            type="text"
            placeholder="账号或邮箱"
            class="w-full px-4 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
            required
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="••••••••"
            class="w-full px-4 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
            required
            minlength="6"
          />
        </div>
        <button
          type="submit"
          class="w-full px-4 py-2 text-sm font-semibold text-white bg-brand rounded-lg hover:bg-brand-dark disabled:opacity-60"
          :disabled="auth.loading"
        >
          {{ auth.loading ? "登录中..." : "登录" }}
        </button>
      </form>
      <p v-if="auth.error" class="text-center text-sm text-red-500">{{ auth.error }}</p>
      <p class="text-xs text-center text-slate-400">
        还没有账号？
        <RouterLink to="/register" class="text-brand hover:underline">去注册</RouterLink>
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useRouter, RouterLink } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
auth.error = null;
const router = useRouter();

const form = reactive({
  accountOrEmail: "",
  password: ""
});

const handleLogin = async () => {
  try {
    await auth.login({ ...form });
    const redirect = router.currentRoute.value.query.redirect as string | undefined;
    router.replace(redirect || "/profile");
  } catch (err) {
    console.error(err);
  }
};
</script>
