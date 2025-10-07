<template>
  <section class="max-w-3xl mx-auto px-6 py-10">
    <header class="mb-8">
      <h1 class="text-2xl font-semibold text-slate-900">个人中心</h1>
      <p class="mt-2 text-sm text-slate-500">更新昵称、头像和简介信息。</p>
    </header>

    <div v-if="!auth.user" class="py-20 text-center text-slate-400">正在加载个人信息...</div>

    <form v-else class="space-y-6" @submit.prevent="handleSave">
      <div class="flex items-center gap-4">
        <img
          :src="avatarPreview"
          alt="Avatar"
          class="h-16 w-16 rounded-full border border-slate-200 object-cover"
        />
        <div class="flex-1">
          <label class="text-sm font-medium text-slate-700" for="avatarUrl">头像 URL</label>
          <input
            id="avatarUrl"
            v-model="form.avatarUrl"
            type="url"
            placeholder="https://..."
            class="mt-1 w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
          />
        </div>
      </div>

      <div>
        <label class="text-sm font-medium text-slate-700" for="displayName">昵称</label>
        <input
          id="displayName"
          v-model="form.displayName"
          type="text"
          class="mt-1 w-full rounded-lg border border-slate-200 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
          required
        />
      </div>

      <div>
        <label class="text-sm font-medium text-slate-700" for="bio">个人简介</label>
        <textarea
          id="bio"
          v-model="form.bio"
          rows="4"
          class="mt-1 w-full rounded-xl border border-slate-200 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-brand"
          placeholder="介绍一下自己，或者记录目标。"
        ></textarea>
      </div>

      <div class="flex items-center justify-between rounded-xl border border-slate-200/60 bg-white px-4 py-3 text-sm text-slate-500">
        <span>账号：<strong class="text-slate-700">{{ auth.user.account }}</strong></span>
        <span>注册时间：{{ formatDate(auth.user.createdAt) }}</span>
      </div>

      <div class="flex items-center gap-3">
        <button
          type="submit"
          class="rounded-lg bg-brand px-6 py-2 text-sm font-semibold text-white hover:bg-brand-dark disabled:opacity-60"
          :disabled="saving"
        >
          {{ saving ? "保存中..." : "保存修改" }}
        </button>
        <span v-if="message" class="text-sm text-green-600">{{ message }}</span>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const saving = ref(false);
const message = ref<string | null>(null);

const form = reactive({
  displayName: "",
  avatarUrl: "",
  bio: ""
});

if (!auth.user) {
  auth.loadProfile();
}

watch(
  () => auth.user,
  (user) => {
    if (user) {
      form.displayName = user.displayName;
      form.avatarUrl = user.avatarUrl || "";
      form.bio = user.bio || "";
    }
  },
  { immediate: true }
);

const avatarPreview = computed(() => form.avatarUrl || auth.user?.avatarUrl || "https://api.dicebear.com/7.x/identicon/svg?seed=" + (auth.user?.account ?? "user"));

const handleSave = async () => {
  saving.value = true;
  message.value = null;
  try {
    await auth.updateProfile({
      displayName: form.displayName,
      avatarUrl: form.avatarUrl || undefined,
      bio: form.bio || undefined
    });
    message.value = "保存成功";
  } catch (err) {
    console.error(err);
    message.value = err instanceof Error ? err.message : "保存失败";
  } finally {
    saving.value = false;
    if (message.value) {
      window.setTimeout(() => {
        message.value = null;
      }, 3000);
    }
  }
};

const formatDate = (value: string) => {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleDateString();
};
</script>
