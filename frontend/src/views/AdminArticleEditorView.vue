<template>
  <section class="px-6 py-10 w-full space-y-8 max-w-[1400px] mx-auto">
    <header class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-3xl font-semibold text-slate-900">写文章</h1>
        <p class="mt-1 text-sm text-slate-500">双栏 Markdown 编辑器，实时预览，辅助你快速完成创作。</p>
      </div>
      <div class="flex items-center gap-3">
        <button
          type="button"
          class="px-4 py-2 text-sm font-medium text-slate-600 border border-slate-200 rounded-lg hover:bg-slate-100"
          @click="resetForm"
        >
          取消
        </button>
        <button
          type="button"
          class="px-4 py-2 text-sm font-semibold text-brand border border-brand/10 rounded-lg bg-brand-light hover:bg-brand"
          @click="handleSaveDraft"
          :disabled="submitting"
        >
          {{ submitting ? "保存中..." : "存为草稿" }}
        </button>
        <button
          type="button"
          class="px-4 py-2 text-sm font-semibold text-white rounded-lg bg-brand hover:bg-brand-dark disabled:opacity-60"
          @click="handlePublish"
          :disabled="submitting"
        >
          {{ submitting ? "发布中..." : "发布" }}
        </button>
      </div>
    </header>

    <form class="space-y-8" @submit.prevent>
      <div class="p-6 bg-white border border-slate-200/70 rounded-2xl shadow-sm space-y-6 max-w-7xl w-full mx-auto">
        <div class="space-y-2">
          <label class="text-sm font-medium text-slate-700" for="title">标题</label>
          <div class="relative">
            <input
              id="title"
              v-model="form.title"
              type="text"
              maxlength="40"
              required
              placeholder="请输入标题"
              class="w-full px-4 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
            />
            <span class="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-slate-400">{{ titleCount }}/40</span>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <label class="text-sm font-medium text-slate-700" for="content">内容</label>
            <span class="text-xs text-slate-400">字数：{{ contentWordCount }}</span>
          </div>
          <MarkdownEditor
            id="content"
            v-model="form.contentMarkdown"
            @word-count-change="handleWordCountChange"
          />
        </div>
      </div>

      <div class="grid gap-6 xl:grid-cols-[minmax(0,1fr)_320px]">
        <div class="space-y-6">
          <div class="p-6 bg-white border border-slate-200/70 rounded-2xl shadow-sm space-y-2">
            <label class="text-sm font-medium text-slate-700" for="summary">摘要</label>
            <textarea
              id="summary"
              v-model="form.summary"
              rows="3"
              placeholder="请输入文章摘要，建议 80 字以内"
              class="w-full px-4 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
            ></textarea>
          </div>

          <div class="p-6 bg-white border border-slate-200/70 rounded-2xl shadow-sm space-y-4">
            <div class="space-y-2">
              <label class="text-sm font-medium text-slate-700" for="tags">标签</label>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="tag in availableTags"
                  :key="tag"
                  type="button"
                  class="px-3 py-1 text-xs rounded-full border transition"
                  :class="form.tags.includes(tag)
                    ? 'bg-brand text-white border-brand'
                    : 'border-slate-200 text-slate-500 hover:border-brand/60'"
                  @click="toggleTag(tag)"
                >
                  {{ tag }}
                </button>
              </div>
              <div v-if="form.tags.length" class="flex flex-wrap gap-2">
                <span
                  v-for="tag in form.tags"
                  :key="`selected-${tag}`"
                  class="px-3 py-1 text-xs text-brand bg-brand/10 rounded-full"
                >
                  {{ tag }}
                </span>
              </div>
              <input
                id="tags"
                v-model="tagsInput"
                type="text"
                placeholder="输入后按 Enter 新增自定义标签"
                class="w-full px-3 py-2 text-xs border border-dashed border-slate-200 rounded-lg focus:outline-none focus:ring-1 focus:ring-brand"
                @keyup.enter.prevent="addTag"
              />
            </div>
          </div>
        </div>

        <aside class="space-y-6">
          <div class="p-6 bg-white border border-slate-200/70 rounded-2xl shadow-sm space-y-4">
            <div class="space-y-2">
              <label class="text-sm font-medium text-slate-700" for="cover">封面</label>
              <div
                id="cover"
                class="flex flex-col items-center justify-center w-full h-36 border border-dashed border-slate-300 rounded-xl text-xs text-slate-400 bg-slate-50/70"
              >
                <span class="mb-1">点击或拖拽上传封面</span>
                <span>（占位功能，后续接入上传接口）</span>
              </div>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium text-slate-700" for="category">分类</label>
              <select
                id="category"
                v-model="form.category"
                class="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
              >
                <option value="">请选择分类</option>
                <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
              </select>
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium text-slate-700" for="author">作者</label>
              <input
                id="author"
                v-model="form.authorName"
                type="text"
                placeholder="作者昵称"
                class="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
              />
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium text-slate-700" for="status">状态</label>
              <select
                id="status"
                v-model="form.status"
                class="w-full px-3 py-2 text-sm border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-brand"
              >
                <option value="DRAFT">草稿</option>
                <option value="PUBLISHED">发布</option>
                <option value="ARCHIVED">归档</option>
              </select>
            </div>

            <div class="space-y-1 text-xs text-slate-400">
              <p>上次保存：{{ lastSavedAt ? lastSavedAt.toLocaleString() : "尚未保存" }}</p>
              <p v-if="success" class="text-green-600">已保存，slug：{{ success.slug }}</p>
              <p v-if="error" class="text-red-500">{{ error }}</p>
            </div>
          </div>
        </aside>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from "vue";
import MarkdownEditor from "@/components/MarkdownEditor.vue";
import type { ArticleDetail, CreateArticlePayload } from "@/services/articleService";
import { createArticle } from "@/services/articleService";

const createDefaultForm = (): CreateArticlePayload & { tags: string[]; category: string | "" } => ({
  title: "",
  summary: "",
  contentMarkdown: "",
  status: "DRAFT",
  authorName: "Liang",
  tags: [],
  category: ""
});

const form = reactive(createDefaultForm());
const submitting = ref(false);
const success = ref<ArticleDetail | null>(null);
const error = ref<string | null>(null);
const lastSavedAt = ref<Date | null>(null);

const categories = ["创作手记", "技术分享", "效率工具", "灵感记录"];

const availableTags = ["AI", "后端", "前端", "生活", "效率"];

const tagsInput = ref("");

const titleCount = computed(() => form.title.trim().length);
const contentWordCount = ref(0);

const resetForm = () => {
  Object.assign(form, createDefaultForm());
  submitting.value = false;
  success.value = null;
  error.value = null;
  lastSavedAt.value = null;
  tagsInput.value = "";
  contentWordCount.value = 0;
};



const persistArticle = async (status: ArticleDetail["status"]) => {
  submitting.value = true;
  error.value = null;
  success.value = null;
  try {
    form.status = status;
    const payload: CreateArticlePayload = {
      title: form.title,
      summary: form.summary,
      contentMarkdown: form.contentMarkdown,
      status: form.status,
      authorName: form.authorName
    };
    const result = await createArticle(payload);
    success.value = result;
    lastSavedAt.value = new Date();
  } catch (err) {
    error.value = err instanceof Error ? err.message : "提交失败";
  } finally {
    submitting.value = false;
  }
};

const handleWordCountChange = (count: number) => {
  contentWordCount.value = count;
};

const handleSaveDraft = async () => {
  await persistArticle("DRAFT");
};

const handlePublish = async () => {
  await persistArticle("PUBLISHED");
};

const toggleTag = (tag: string) => {
  if (form.tags.includes(tag)) {
    form.tags = form.tags.filter((item) => item !== tag);
  } else {
    form.tags = [...form.tags, tag];
  }
};

const addTag = () => {
  const value = tagsInput.value.trim();
  if (!value) return;
  if (!form.tags.includes(value)) {
    form.tags = [...form.tags, value];
  }
  tagsInput.value = "";
};

</script>
