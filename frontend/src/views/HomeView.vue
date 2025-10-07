<template>
  <section class="px-6 py-10 mx-auto max-w-5xl">
    <header class="mb-8">
      <h1 class="text-3xl font-semibold tracking-tight text-slate-900">Liang Blog</h1>
      <p class="mt-2 text-slate-500">发现精选文章，记录灵感，与社区交流。</p>
    </header>

    <div v-if="loading" class="py-20 text-center text-slate-500">加载文章中...</div>
    <div v-else-if="error" class="py-20 text-center text-red-500">{{ error }}</div>

    <article v-else class="space-y-6">
      <div
        v-for="post in articles"
        :key="post.slug"
        class="p-6 bg-white border border-slate-200/60 rounded-xl shadow-sm hover:border-brand transition"
      >
        <RouterLink :to="{ name: 'article', params: { slug: post.slug } }" class="text-xl font-medium text-brand">
          {{ post.title }}
        </RouterLink>
        <p class="mt-2 text-sm text-slate-500">
          {{ formatPublishedAt(post.publishedAt) }} · {{ post.authorName || "Liang" }}
        </p>
        <p class="mt-3 text-slate-600 line-clamp-3">
          {{ post.summary || "这篇文章还没有摘要，敬请期待。" }}
        </p>
      </div>
      <p v-if="articles.length === 0" class="py-20 text-center text-slate-500">尚未发布任何文章，欢迎稍后再来。</p>
    </article>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { RouterLink } from "vue-router";
import type { ArticleSummary } from "@/services/articleService";
import { fetchPublishedArticles } from "@/services/articleService";

const loading = ref(true);
const error = ref<string | null>(null);
const articles = ref<ArticleSummary[]>([]);

onMounted(async () => {
  try {
    articles.value = await fetchPublishedArticles();
  } catch (err) {
    error.value = err instanceof Error ? err.message : "加载文章失败";
  } finally {
    loading.value = false;
  }
});

const formatPublishedAt = (value: string | null) => {
  if (!value) {
    return "草稿";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleDateString();
};
</script>
