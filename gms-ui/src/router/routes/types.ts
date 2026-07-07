/**
 * 应用路由记录类型定义
 * 扩展 vue-router 路由项，支持懒加载组件与导航守卫类型。
 */
import { defineComponent } from 'vue';
import type { RouteMeta, NavigationGuard } from 'vue-router';

export type Component<T = any> =
  | ReturnType<typeof defineComponent>
  | (() => Promise<typeof import('*.vue')>)
  | (() => Promise<T>);

export interface AppRouteRecordRaw {
  path: string;
  name?: string | symbol;
  meta?: RouteMeta;
  redirect?: string;
  component: Component | string;
  children?: AppRouteRecordRaw[];
  alias?: string | string[];
  props?: Record<string, any>;
  beforeEnter?: NavigationGuard | NavigationGuard[];
  fullPath?: string;
}
