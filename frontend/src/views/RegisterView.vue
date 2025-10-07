<template>
  <section class="min-h-[70vh] flex items-center justify-center px-6 py-10 bg-gradient-to-b from-slate-50 to-white">
    <div class="w-full max-w-md space-y-6 rounded-2xl border border-slate-200/60 bg-white p-8 shadow-sm">
      <header class="space-y-2 text-center">
        <h1 class="text-2xl font-semibold text-slate-900">注册新账号</h1>
        <p class="text-sm text-slate-500">创建账号后即可同步 TodoList 与发表评论。</p>
      </header>
      <form class="space-y-4" @submit.prevent="handleRegister">
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="account">账号</label>
          <input
            id="account"
            v-model="form.account"
            type="text"
            placeholder="用户名（3-30个字符）"
            class="w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
            required
            minlength="3"
            maxlength="30"
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="email">邮箱</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            placeholder="you@example.com"
            class="w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
            required
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="displayName">昵称</label>
          <input
            id="displayName"
            v-model="form.displayName"
            type="text"
            placeholder="展示给他人的名称"
            class="w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
            required
          />
        </div>
        <div class="space-y-1">
          <label class="text-sm font-medium text-slate-700" for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            placeholder="至少 6 位"
            class="w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
            required
            minlength="6"
          />
        </div>
        <button
          type="submit"
          class="w-full rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white hover:bg-brand-dark disabled:opacity-60"
          :disabled="auth.loading"
        >
          {{ auth.loading ? "注册中..." : "注册" }}
        </button>
      </form>
      <p v-if="auth.error" class="text-center text-sm text-red-500">{{ auth.error }}</p>
      <p class="text-center text-xs text-slate-400">
        已有账号？
        <RouterLink to="/signin" class="text-brand hover:underline">去登录</RouterLink>
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
  account: "",
  email: "",
  password: "",
  displayName: ""
});

const handleRegister = async () => {
  try {
    await auth.register({ ...form });
    router.replace({ name: "profile" });
  } catch (err) {
    console.error(err);
  }
};
</script>
