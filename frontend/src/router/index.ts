import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import ArticleView from "@/views/ArticleView.vue";
import TodoView from "@/views/TodoView.vue";
import SignInView from "@/views/SignInView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ProfileView from "@/views/ProfileView.vue";
import AdminArticleEditorView from "@/views/AdminArticleEditorView.vue";
import { useAuthStore } from "@/stores/auth";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "home",
    component: HomeView
  },
  {
    path: "/articles/:slug",
    name: "article",
    component: ArticleView,
    props: true
  },
  {
    path: "/todo",
    name: "todo",
    component: TodoView
  },
  {
    path: "/signin",
    name: "signin",
    component: SignInView
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView
  },
  {
    path: "/profile",
    name: "profile",
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: "/admin/articles/new",
    name: "admin-article-new",
    component: AdminArticleEditorView
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore();
  if (to.meta.requiresAuth && !auth.token) {
    next({ name: "signin", query: { redirect: to.fullPath } });
    return;
  }
  if ((to.name === "signin" || to.name === "register") && auth.token) {
    next({ name: "profile" });
    return;
  }
  next();
});

export default router;
