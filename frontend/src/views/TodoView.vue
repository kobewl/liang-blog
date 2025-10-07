<template>
  <section class="w-full min-h-screen bg-slate-100/60 px-6 py-6 lg:px-10">
    <header class="flex flex-col gap-2 py-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-semibold text-slate-900">任务面板</h1>
          <p class="text-slate-500">全屏编辑每日/每周/每月任务，支持 Markdown 与一键插入待办。</p>
        </div>
        <nav class="flex gap-2">
          <button
            v-for="scope in scopes"
            :key="scope"
            class="px-4 py-2 text-sm font-medium rounded-full border transition"
            :class="scope === activeScope ? 'bg-brand text-white border-brand' : 'text-slate-600 border-transparent hover:border-brand/40'
            "
            @click="() => switchScope(scope)"
          >
            {{ scopeLabels[scope] }}
          </button>
        </nav>
      </div>
    </header>

    <div class="grid gap-6 lg:grid-cols-[minmax(0,1.75fr)_minmax(0,1fr)] xl:grid-cols-[minmax(0,1.8fr)_minmax(0,1fr)] h-[calc(100vh-180px)]">
      <!-- Editor Column -->
      <div class="flex flex-col bg-white border border-slate-200 rounded-3xl shadow-sm overflow-hidden">
        <div class="flex items-center justify-between px-6 py-4 border-b border-slate-100">
          <div class="flex items-center gap-3">
            <span class="text-sm font-semibold text-brand/80">
              {{ editingEntryId ? '编辑任务' : '新建任务' }} · {{ scopeLabels[activeScope] }}
            </span>
            <span
              v-if="selectedCategoryLabel"
              class="px-2 py-1 text-xs font-medium bg-brand/10 text-brand rounded-full"
            >
              {{ selectedCategoryLabel }}
            </span>
          </div>
          <div class="flex items-center gap-2 text-slate-400 text-xs">
            <button class="hover:text-brand" @click="handleTogglePreview">{{ showPreview ? '隐藏预览' : '显示预览' }}</button>
            <span>·</span>
            <button class="hover:text-brand" @click="resetEditor">新建</button>
          </div>
        </div>

        <div class="grid flex-1 grid-rows-[auto_auto_minmax(0,1fr)_auto] lg:grid-rows-[auto_auto_minmax(0,1fr)_auto_auto]">
          <!-- Title & Tags -->
          <div class="px-6 py-4 border-b border-slate-100 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
            <div class="flex-1">
              <label class="text-xs font-semibold text-slate-400 uppercase tracking-[0.2em]">任务标题</label>
              <input
                v-model="titleDraft"
                type="text"
                class="mt-2 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-brand/50"
                placeholder="写个醒目的标题，例：AI 项目例会筹备"
              />
            </div>
            <div class="flex flex-col gap-2 lg:w-64">
              <label class="text-xs font-semibold text-slate-400 uppercase tracking-[0.2em]">标签</label>
              <div class="flex flex-wrap gap-2">
                <button
                  v-for="tag in tagSuggestions"
                  :key="tag"
                  class="px-3 py-1 text-xs rounded-full border transition"
                  :class="selectedCategoryName === tag ? 'bg-brand text-white border-brand' : 'border-slate-200 text-slate-600 hover:border-brand/40'"
                  @click="() => toggleSelectedCategory(tag)"
                >
                  {{ tag }}
                </button>
                <button
                  class="px-3 py-1 text-xs rounded-full border border-dashed border-slate-300 text-slate-500 hover:border-brand/60"
                  @click="promptNewTag"
                >
                  + 新标签
                </button>
              </div>
            </div>
          </div>

          <!-- Toolbar -->
          <div class="px-6 py-3 border-b border-slate-100 flex items-center gap-3 text-slate-500 text-sm">
            <span class="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">快捷插入</span>
            <button class="toolbar-btn" @click="() => appendHeading()">
              <span class="text-sm font-semibold">H2</span>
            </button>
            <button class="toolbar-btn" @click="() => appendCheckbox('新的待办')">
              <span class="text-sm">☑︎</span>
            </button>
            <button class="toolbar-btn" @click="() => appendBullet()">
              <span class="text-sm">•</span>
            </button>
            <button class="toolbar-btn" @click="() => appendQuote()">
              <span class="text-sm">“”</span>
            </button>
            <button class="toolbar-btn" @click="() => appendCodeBlock()">
              <span class="text-sm">{ }</span>
            </button>
            <span class="ml-auto text-xs text-slate-400">按 Enter 快速添加待办</span>
          </div>

          <!-- Editor -->
          <div class="grid lg:grid-cols-[minmax(0,1fr)] xl:grid-cols-[minmax(0,1fr)_minmax(0,1fr)] flex-1 overflow-hidden">
            <div class="flex flex-col border-b xl:border-b-0 xl:border-r border-slate-100">
              <textarea
                ref="editorRef"
                v-model="contentDraft"
                class="h-full min-h-[280px] flex-1 resize-none bg-white px-6 py-5 text-sm leading-7 text-slate-800 outline-none"
                placeholder="记录想法、拆解待办，或使用快捷按钮插入格式..."
                @keydown.enter.prevent="handleQuickEnter"
              ></textarea>
              <div class="px-6 pb-3 pt-3 border-t border-slate-100 flex items-center gap-3">
                <input
                  v-model="quickTodoInput"
                  type="text"
                  class="flex-1 rounded-lg border border-slate-200 bg-slate-50 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand/40"
                  placeholder="输入待办，按 Enter 添加"
                  @keydown.enter.prevent="handleQuickTodoAdd"
                />
                <button
                  class="rounded-lg bg-brand px-4 py-2 text-sm font-semibold text-white hover:bg-brand-dark"
                  @click="handleQuickTodoAdd"
                >
                  添加
                </button>
              </div>
              <div class="px-6 pb-4 text-xs text-slate-400">点击标签即可添加待办：</div>
              <div class="px-6 pb-4 flex flex-wrap gap-2">
                <button
                  v-for="tag in quickAddTags"
                  :key="tag"
                  class="px-3 py-1 text-xs rounded-full border border-slate-200 text-slate-600 hover:border-brand/40 hover:text-brand"
                  @click="() => appendCheckbox(tag)"
                >
                  # {{ tag }}
                </button>
              </div>
            </div>

            <div v-if="showPreview" class="hidden xl:flex flex-col bg-slate-50/80">
              <header class="px-6 py-3 border-b border-slate-200 text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                实时预览
              </header>
              <div class="prose prose-slate max-w-none flex-1 overflow-auto px-6 py-5 text-sm" v-html="previewHtml"></div>
            </div>
          </div>

          <!-- Actions -->
          <div class="px-6 py-4 border-t border-slate-100 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div class="text-xs text-slate-400" v-if="saveMessage">{{ saveMessage }}</div>
            <div class="text-xs text-red-500" v-if="editorError">{{ editorError }}</div>
            <div class="flex items-center gap-3 sm:ml-auto">
              <button
                class="rounded-lg border border-slate-200 px-4 py-2 text-sm text-slate-600 hover:border-brand/40"
                @click="resetEditor"
              >
                清空
              </button>
              <button
                class="rounded-lg bg-brand px-6 py-2 text-sm font-semibold text-white hover:bg-brand-dark disabled:opacity-60"
                :disabled="saving || !canSave"
                @click="handleSave"
              >
                {{ saving ? '保存中...' : editingEntryId ? '更新任务' : '保存任务' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- History Column -->
      <div class="flex flex-col bg-white border border-slate-200 rounded-3xl shadow-sm overflow-hidden">
        <div class="px-6 py-4 border-b border-slate-100 flex flex-col gap-3">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-medium text-slate-900">历史记录</h2>
            <span class="text-xs text-slate-400">共 {{ entries.length }} 条</span>
          </div>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="chip in categoryChips"
              :key="chip.key"
              class="px-3 py-1 text-xs rounded-full border transition"
              :class="categoryFilter === chip.key ? 'bg-brand text-white border-brand' : 'border-slate-200 text-slate-600 hover:border-brand/40'"
              @click="() => (categoryFilter = chip.key)"
            >
              {{ chip.label }} ({{ chip.count }})
            </button>
          </div>
        </div>
        <div class="flex-1 overflow-auto px-6 py-4 space-y-4">
          <div v-if="entriesLoading" class="py-10 text-center text-slate-500">加载历史任务中...</div>
          <div v-else-if="entriesError" class="py-10 text-center text-red-500">{{ entriesError }}</div>
          <div v-else-if="filteredEntries.length === 0" class="py-10 text-center text-slate-400">
            暂无历史记录，保存后这里会显示最近的任务拆解。
          </div>
          <article
            v-for="entry in filteredEntries"
            :key="entry.id"
            class="rounded-2xl border border-slate-200/70 bg-white px-5 py-4 shadow-sm hover:border-brand/40 transition cursor-pointer"
            @click="() => loadEntry(entry)"
          >
            <header class="flex items-start justify-between gap-3">
              <div>
                <h3 class="text-base font-semibold text-slate-900">{{ entry.title }}</h3>
                <p class="text-xs text-slate-400 mt-1">
                  {{ formatDate(entry.updatedAt) }} · {{ scopeLabels[activeScope] }}
                </p>
              </div>
              <span
                v-if="entry.categoryName"
                class="px-2 py-1 text-xs font-medium bg-brand/10 text-brand rounded-full whitespace-nowrap"
              >
                {{ entry.categoryName }}
              </span>
            </header>
            <ul v-if="extractChecklist(entry).length" class="mt-3 space-y-2 text-sm text-slate-600">
              <li
                v-for="item in extractChecklist(entry)"
                :key="`${entry.id}-${item.lineIndex}`"
                class="flex items-start gap-2"
              >
                <button
                  type="button"
                  class="mt-[2px] inline-flex h-4 w-4 items-center justify-center rounded border transition focus:outline-none focus:ring-2 focus:ring-brand/40"
                  :class="item.done ? 'border-brand bg-brand text-white' : 'border-slate-300 text-transparent hover:border-brand/60 hover:text-brand'"
                  @click.stop="() => toggleChecklist(entry, item)"
                >
                  ✓
                </button>
                <span :class="item.done ? 'line-through text-slate-400' : ''">{{ item.text }}</span>
              </li>
            </ul>
            <div v-else class="mt-3 text-sm text-slate-500 leading-relaxed truncate">
              {{ summarize(entry.contentMarkdown) }}
            </div>
          </article>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from "vue";
import DOMPurify from "dompurify";
import { marked } from "marked";
import type {
  TodoCategorySummary,
  TodoEntry,
  TodoEntryPayload,
  TodoScope
} from "@/services/todoService";
import {
  createTodoEntry,
  fetchTodoCategories,
  fetchTodoEntries,
  updateTodoEntry
} from "@/services/todoService";
import { ApiError } from "@/services/apiClient";

marked.setOptions({ breaks: true });

const scopes: TodoScope[] = ["daily", "weekly", "monthly"];
const scopeLabels: Record<TodoScope, string> = {
  daily: "每日",
  weekly: "每周",
  monthly: "每月"
};

const activeScope = ref<TodoScope>("daily");
const categories = ref<TodoCategorySummary[]>([]);
const entries = ref<TodoEntry[]>([]);
const entriesLoading = ref(false);
const entriesError = ref<string | null>(null);
const categoryFilter = ref<"all" | "none" | number>("all");

const editingEntryId = ref<number | null>(null);
const titleDraft = ref("");
const contentDraft = ref("");
const selectedCategoryName = ref<string | null>(null);
const quickTodoInput = ref("");
const editorRef = ref<HTMLTextAreaElement | null>(null);
const showPreview = ref(true);
const checklistUpdatingKey = ref<string | null>(null);

const saving = ref(false);
const saveMessage = ref<string | null>(null);
const editorError = ref<string | null>(null);
let saveTimer: number | null = null;

const tagPresets = ["AI 项目", "需求拆解", "复盘", "灵感速记", "会议笔记"];

const tagSuggestions = computed(() => {
  const names = new Set<string>();
  categories.value.forEach((category) => {
    if (category.id !== null) {
      names.add(category.name);
    }
  });
  tagPresets.forEach((preset) => names.add(preset));
  return Array.from(names).slice(0, 12);
});

const quickAddTags = computed(() => tagSuggestions.value.slice(0, 8));

const previewHtml = computed(() => {
  const raw = contentDraft.value || "";
  return DOMPurify.sanitize(marked.parse(raw));
});

const selectedCategoryLabel = computed(() => selectedCategoryName.value || "无标签");

const resolvedTitle = computed(() => {
  const direct = titleDraft.value.trim();
  if (direct.length > 0) {
    return direct;
  }
  const firstLine = contentDraft.value
    .split("\n")
    .map((line) => line.trim())
    .find((line) => line.length > 0);
  if (!firstLine) {
    return "";
  }
  return firstLine.replace(/^[-*]\s*(\[[xX ]\]\s*)?/, "").slice(0, 80) || firstLine;
});

const canSave = computed(() => resolvedTitle.value.length > 0 && contentDraft.value.trim().length > 0);

const categoryChips = computed(() => {
  const total = entries.value.length;
  const chips: Array<{ key: "all" | "none" | number; label: string; count: number }> = [
    { key: "all", label: "全部", count: total }
  ];

  categories.value.forEach((category) => {
    if (category.id === null) {
      chips.push({ key: "none", label: category.name, count: category.entryCount });
    } else {
      chips.push({ key: category.id, label: category.name, count: category.entryCount });
    }
  });

  const hasNoTagChip = chips.some((chip) => chip.key === "none");
  if (!hasNoTagChip) {
    const count = entries.value.filter((entry) => entry.categoryId === null).length;
    chips.push({ key: "none", label: "无标签", count });
  }

  return chips;
});

const filteredEntries = computed(() => {
  if (categoryFilter.value === "all") {
    return entries.value;
  }
  if (categoryFilter.value === "none") {
    return entries.value.filter((entry) => entry.categoryId === null);
  }
  return entries.value.filter((entry) => entry.categoryId === categoryFilter.value);
});

const loadScopeData = async () => {
  await Promise.all([loadCategories(), loadEntries()]);
};

const loadCategories = async () => {
  try {
    categories.value = await fetchTodoCategories(activeScope.value);
  } catch (err) {
    console.error(err);
  }
};

const loadEntries = async () => {
  entriesLoading.value = true;
  entriesError.value = null;
  try {
    entries.value = await fetchTodoEntries(activeScope.value);
  } catch (err) {
    const message = err instanceof Error ? err.message : "加载任务失败";
    entriesError.value = message;
  } finally {
    entriesLoading.value = false;
  }
};

const switchScope = (scope: TodoScope) => {
  if (activeScope.value === scope) {
    return;
  }
  activeScope.value = scope;
};

watch(activeScope, async () => {
  resetEditor();
  categoryFilter.value = "all";
  await loadScopeData();
});

onMounted(async () => {
  await loadScopeData();
});

const appendHeading = () => {
  appendLine("## 小节标题");
};

const appendCheckbox = (label: string) => {
  appendLine(`- [ ] ${label}`);
};

const appendBullet = () => {
  appendLine("- 列出要点");
};

const appendQuote = () => {
  appendLine("> 灵感或记录");
};

const appendCodeBlock = () => {
  appendLine("```\n示例代码或数据\n```\n");
};

const appendLine = (line: string) => {
  if (!contentDraft.value) {
    contentDraft.value = line;
    return;
  }
  const needsNewline = !contentDraft.value.endsWith("\n");
  contentDraft.value = `${contentDraft.value}${needsNewline ? "\n" : ""}${line}`;
};

const handleQuickEnter = (event: KeyboardEvent) => {
  if (event.shiftKey) {
    contentDraft.value += "\n";
    return;
  }
  event.preventDefault();
  handleQuickTodoAdd();
};

const handleQuickTodoAdd = () => {
  const text = quickTodoInput.value.trim();
  if (!text) {
    return;
  }
  appendCheckbox(text);
  quickTodoInput.value = "";
  nextTick(() => {
    editorRef.value?.focus();
  });
};

const toggleSelectedCategory = (tag: string) => {
  if (selectedCategoryName.value === tag) {
    selectedCategoryName.value = null;
  } else {
    selectedCategoryName.value = tag;
  }
};

const promptNewTag = () => {
  const value = window.prompt("请输入新的标签名称", "");
  if (!value) {
    return;
  }
  selectedCategoryName.value = value.trim();
};

const handleSave = async () => {
  if (!canSave.value || saving.value) {
    return;
  }
  saving.value = true;
  editorError.value = null;
  try {
    const payload: TodoEntryPayload = {
      title: resolvedTitle.value,
      contentMarkdown: contentDraft.value.trim(),
      categoryName: selectedCategoryName.value || undefined
    };

    const response = editingEntryId.value
      ? await updateTodoEntry(activeScope.value, editingEntryId.value, payload)
      : await createTodoEntry(activeScope.value, payload);

    editingEntryId.value = response.id;
    selectedCategoryName.value = response.categoryName;
    titleDraft.value = response.title;
    saveMessage.value = "已保存至历史任务";
    if (saveTimer) {
      window.clearTimeout(saveTimer);
    }
    saveTimer = window.setTimeout(() => {
      saveMessage.value = null;
    }, 3000);

    await loadScopeData();
  } catch (err) {
    if (err instanceof ApiError) {
      editorError.value = err.message;
    } else if (err instanceof Error) {
      editorError.value = err.message;
    } else {
      editorError.value = "保存失败";
    }
  } finally {
    saving.value = false;
  }
};

const resetEditor = () => {
  editingEntryId.value = null;
  titleDraft.value = "";
  contentDraft.value = "";
  selectedCategoryName.value = null;
  quickTodoInput.value = "";
};

const loadEntry = (entry: TodoEntry) => {
  editingEntryId.value = entry.id;
  titleDraft.value = entry.title;
  contentDraft.value = entry.contentMarkdown;
  selectedCategoryName.value = entry.categoryName || null;
  nextTick(() => editorRef.value?.focus());
};

const handleTogglePreview = () => {
  showPreview.value = !showPreview.value;
};

interface ChecklistItem {
  lineIndex: number;
  text: string;
  done: boolean;
}

const CHECKBOX_PATTERN = /^-\s*\[[ xX]\]/;
const CHECKBOX_PREFIX = /^-\s*\[[ xX]\]\s*/;

const extractChecklist = (entry: TodoEntry): ChecklistItem[] => {
  const lines = entry.contentMarkdown.split("\n");
  const checklist: ChecklistItem[] = [];

  lines.forEach((line, index) => {
    const trimmed = line.trimStart();
    if (CHECKBOX_PATTERN.test(trimmed)) {
      const text = trimmed.replace(CHECKBOX_PREFIX, "");
      const done = /^-\s*\[[xX]\]/.test(trimmed);
      checklist.push({ lineIndex: index, text, done });
    }
  });

  return checklist.slice(0, 6);
};

const toggleChecklist = async (entry: TodoEntry, item: ChecklistItem) => {
  const key = `${entry.id}-${item.lineIndex}`;
  if (checklistUpdatingKey.value === key) {
    return;
  }
  checklistUpdatingKey.value = key;

  try {
    const lines = entry.contentMarkdown.split("\n");
    const originalLine = lines[item.lineIndex];
    if (!originalLine) {
      return;
    }

    const leadingWhitespace = originalLine.match(/^\s*/)?.[0] ?? "";
    const trimmed = originalLine.slice(leadingWhitespace.length);

    if (!CHECKBOX_PATTERN.test(trimmed)) {
      return;
    }

    const contentWithoutPrefix = trimmed.replace(CHECKBOX_PREFIX, "");
    const nextPrefix = item.done ? "- [ ]" : "- [x]";
    const nextLine = contentWithoutPrefix.length
      ? `${nextPrefix} ${contentWithoutPrefix}`
      : nextPrefix;

    lines[item.lineIndex] = `${leadingWhitespace}${nextLine}`;
    const updatedMarkdown = lines.join("\n");

    const payload: TodoEntryPayload = {
      title: entry.title,
      contentMarkdown: updatedMarkdown,
      categoryName: entry.categoryName ?? undefined
    };

    const updated = await updateTodoEntry(activeScope.value, entry.id, payload);

    const index = entries.value.findIndex((candidate) => candidate.id === entry.id);
    if (index !== -1) {
      entries.value.splice(index, 1, updated);
    }

    if (editingEntryId.value === entry.id) {
      contentDraft.value = updated.contentMarkdown;
      titleDraft.value = updated.title;
      selectedCategoryName.value = updated.categoryName || null;
    }
  } catch (err) {
    const message = err instanceof Error ? err.message : "更新失败";
    window.alert(message);
    console.error(err);
  } finally {
    checklistUpdatingKey.value = null;
  }
};

const summarize = (markdown: string) => {
  const plain = markdown.replace(/[#>*`\-\[\]]/g, "").replace(/\n/g, " ");
  return plain.length > 120 ? `${plain.slice(0, 120)}...` : plain;
};

const formatDate = (value: string) => {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
};
</script>

<style scoped>
.toolbar-btn {
  @apply rounded-full border border-slate-200 px-3 py-1 hover:border-brand/40 hover:text-brand transition;
}
</style>
