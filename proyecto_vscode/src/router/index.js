import { createRouter, createWebHashHistory } from "vue-router";

import DashboardView from "../views/DashboardView.vue";
import LoginView from "../views/LoginView.vue";
import OrdenesCompraView from "../views/OrdenesCompraView.vue";
import HojasVidaProveedoresView from "../views/HojasVidaProveedoresView.vue";
import AuthService from "../services/AuthService";

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    meta: {
      requiereAuth: true,
    },
  },
  {
    path: "/ordenes-compra",
    name: "ordenes-compra",
    component: OrdenesCompraView,
    meta: {
      requiereAuth: true,
    },
  },
  {
    path: "/hojas-vida-proveedores",
    name: "hojas-vida-proveedores",
    component: HojasVidaProveedoresView,
    meta: {
      requiereAuth: true,
    },
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/login",
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.meta.requiereAuth && !AuthService.estaAutenticado()) {
    next("/login");
    return;
  }

  if (to.path === "/login" && AuthService.estaAutenticado()) {
    next("/dashboard");
    return;
  }

  next();
});

export default router;
