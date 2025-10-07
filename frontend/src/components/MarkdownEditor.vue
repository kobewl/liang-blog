<template>
  <div class="space-y-3">
    <div class="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2">
      <div class="flex flex-wrap items-center gap-2 text-sm">
        <EditorButton icon="H" label="标题" @click="applySyntax('# ')" />
        <EditorButton icon="B" label="加粗" @click="wrapSelection('**', '**')" />
        <EditorButton icon="I" label="斜体" @click="wrapSelection('*', '*')" />
        <EditorButton icon="S" label="删除线" @click="wrapSelection('~~', '~~')" />
        <EditorButton icon="❝" label="引用" @click="applySyntax('> ')" />
        <EditorButton icon="•" label="无序" @click="applySyntax('- ')" />
        <EditorButton icon="1." label="有序" @click="applySyntax('1. ')" />
        <EditorButton icon="[]" label="任务" @click="applySyntax('- [ ] ')" />
        <EditorButton icon="</>" label="代码" @click="wrapSelection('```\n', '\n```')" />
        <EditorButton icon="_" label="分割线" @click="insertAtCursor('\n\n---\n\n')" />
        <EditorButton icon="[]()" label="链接" @click="wrapSelection('[', '](https://)')" />
        <EditorButton icon="IMG" label="图片" @click="wrapSelection('![描述](', ')')" />
      </div>
      <div class="flex items-center gap-4 text-xs text-slate-500">
        <span>字数：{{ wordCount }}</span>
        <label class="flex items-center gap-1">
          <input v-model="syncScroll" type="checkbox" class="h-4 w-4 rounded border-slate-300" />
          同步滚动
        </label>
      </div>
    </div>

    <div class="grid gap-0 overflow-hidden rounded-2xl border border-slate-200 bg-white lg:grid-cols-2 lg:grid">
      <div class="flex flex-col border-b border-slate-200/80 lg:border-b-0 lg:border-r lg:min-h-[480px]">
        <textarea
          ref="editorRef"
          v-model="content"
          class="min-h-[320px] lg:min-h-[480px] flex-1 resize-none bg-transparent px-6 py-4 font-mono text-sm text-slate-700 focus:outline-none"
          spellcheck="false"
          @scroll="handleScroll('editor', $event)"
        ></textarea>
      </div>
      <div
        ref="previewRef"
        class="markdown-preview markdown-body overflow-y-auto px-8 py-6 lg:min-h-[480px] bg-white"
        v-html="renderedHtml"
        @scroll="handleScroll('preview', $event)"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineComponent, h, onMounted, ref, watch } from "vue";
import { marked } from "marked";
import DOMPurify from "dompurify";
import hljs from "highlight.js";

const props = defineProps<{ modelValue: string }>();
const emit = defineEmits<{ (event: "update:modelValue", value: string): void; (event: "word-count-change", value: number): void }>();

const content = ref(props.modelValue);
const syncScroll = ref(true);
const editorRef = ref<HTMLTextAreaElement | null>(null);
const previewRef = ref<HTMLDivElement | null>(null);

marked.setOptions({
  breaks: true,
  gfm: true,
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value;
    }
    return hljs.highlightAuto(code).value;
  }
});

const renderedHtml = computed(() => {
  const raw = marked.parse(content.value || "");
  return DOMPurify.sanitize(raw || "");
});

const wordCount = computed(() => {
  return content.value ? content.value.replace(/\s+/g, " ").trim().length : 0;
});

watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal !== content.value) {
      content.value = newVal;
    }
  }
);

watch(content, (value) => {
  emit("update:modelValue", value);
  emit("word-count-change", wordCount.value);
});

const insertAtCursor = (text: string) => {
  const textarea = editorRef.value;
  if (!textarea) {
    content.value += text;
    return;
  }
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const before = content.value.slice(0, start);
  const after = content.value.slice(end);
  content.value = `${before}${text}${after}`;
  focusTextarea(start + text.length);
};

const wrapSelection = (prefix: string, suffix: string) => {
  const textarea = editorRef.value;
  if (!textarea) {
    content.value = `${prefix}${content.value}${suffix}`;
    return;
  }
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const selected = content.value.slice(start, end) || "文本";
  const before = content.value.slice(0, start);
  const after = content.value.slice(end);
  content.value = `${before}${prefix}${selected}${suffix}${after}`;
  focusTextarea(start + prefix.length + selected.length + suffix.length);
};

const applySyntax = (prefix: string) => {
  const textarea = editorRef.value;
  if (!textarea) {
    content.value = `${prefix}${content.value}`;
    return;
  }
  const start = textarea.selectionStart;
  const lineStart = content.value.lastIndexOf("\n", start - 1) + 1;
  const before = content.value.slice(0, lineStart);
  const after = content.value.slice(lineStart);
  content.value = `${before}${prefix}${after}`;
  focusTextarea(start + prefix.length);
};

const focusTextarea = (cursor: number) => {
  requestAnimationFrame(() => {
    const textarea = editorRef.value;
    if (textarea) {
      textarea.focus();
      textarea.setSelectionRange(cursor, cursor);
    }
  });
};

const handleScroll = (source: "editor" | "preview", event: Event) => {
  if (!syncScroll.value) return;
  const editor = editorRef.value;
  const preview = previewRef.value;
  if (!editor || !preview) return;

  const ratio =
    source === "editor"
      ? editor.scrollTop / (editor.scrollHeight - editor.clientHeight || 1)
      : preview.scrollTop / (preview.scrollHeight - preview.clientHeight || 1);

  if (source === "editor") {
    preview.scrollTop = ratio * (preview.scrollHeight - preview.clientHeight);
  } else {
    editor.scrollTop = ratio * (editor.scrollHeight - editor.clientHeight);
  }
};

onMounted(() => {
  emit("word-count-change", wordCount.value);
});

const EditorButton = defineComponent({
  name: "EditorButton",
  props: {
    icon: { type: String, required: true },
    label: { type: String, required: true }
  },
  emits: ["click"],
  setup(props, { emit }) {
    const handleClick = () => emit("click");
    return () =>
      h(
        "button",
        {
          type: "button",
          title: props.label,
          class: "flex items-center justify-center rounded-lg border border-slate-200 bg-white px-2 py-1 text-xs font-semibold text-slate-600 shadow-sm transition hover:border-brand/60 hover:text-brand",
          onClick: handleClick
        },
        props.icon
      );
  }
});
</script>

<style scoped>
.markdown-preview :deep(pre) {
  @apply rounded-xl bg-slate-900 text-slate-100 p-4 overflow-auto;
}
.markdown-preview :deep(code) {
  @apply bg-slate-100 text-slate-800 px-1 py-0.5 rounded;
}
.markdown-preview :deep(table) {
  @apply w-full border border-slate-200;
}
.markdown-preview :deep(th),
.markdown-preview :deep(td) {
  @apply border border-slate-200 px-3 py-2 text-left;
}
</style>
