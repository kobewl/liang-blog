<template>
  <article class="px-6 py-10 mx-auto max-w-3xl space-y-10">
    <div v-if="loading" class="py-20 text-center text-slate-500">加载文章中...</div>
    <div v-else-if="error" class="py-20 text-center text-red-500">{{ error }}</div>

    <template v-else>
      <header>
        <p class="text-sm uppercase tracking-[0.2em] text-brand">ARTICLE</p>
        <h1 class="mt-3 text-4xl font-semibold text-slate-900">{{ article?.title }}</h1>
        <p class="mt-2 text-slate-500">
          发布于 {{ formatPublishedAt(article?.publishedAt) }} · {{ article?.authorName || "Liang" }}
        </p>
      </header>

      <section class="prose prose-slate max-w-none" v-html="article?.contentHtml"></section>

      <section class="p-6 border border-slate-200/60 rounded-xl bg-slate-50">
        <header class="flex items-center justify-between">
          <h2 class="text-lg font-medium text-slate-900">评论</h2>
          <RouterLink v-if="!isAuthenticated" :to="{ name: 'signin' }" class="text-sm text-brand hover:underline">
            登录以参与讨论
          </RouterLink>
        </header>
        <p v-if="isAuthenticated" class="mt-4 text-sm text-slate-500">
          Markdown 支持即将到来。你可以在下方工具栏中插入标题、列表和代码块。
        </p>
        <div v-else class="mt-4 text-sm text-slate-500">登录后可以发表评论并与作者互动。</div>
      </section>

      <section class="p-6 border border-brand/20 rounded-xl bg-white">
        <h3 class="text-base font-semibold text-slate-900">AI 推荐阅读</h3>
        <p class="mt-2 text-sm text-slate-600">
          当 Spring AI 集成完成后，这里将展示根据文章语义生成的推荐摘要与相关文章。
        </p>
      </section>
    </template>
  </article>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from "vue";
import { useRoute, RouterLink } from "vue-router";
import type { ArticleDetail } from "@/services/articleService";
import { fetchArticleBySlug } from "@/services/articleService";

const route = useRoute();
const loading = ref(true);
const error = ref<string | null>(null);
const article = ref<ArticleDetail>();

const loadArticle = async (slug: string) => {
  loading.value = true;
  error.value = null;
  try {
    article.value = await fetchArticleBySlug(slug);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "加载文章失败";
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadArticle(route.params.slug as string);
});

watch(
  () => route.params.slug,
  (slug) => {
    if (typeof slug === "string") {
      loadArticle(slug);
    }
  }
);

const isAuthenticated = false;

const formatPublishedAt = (value?: string | null) => {
  if (!value) {
    return "草稿";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
};
</script>
