/**
 * 应用全局设置状态类型定义
 * 描述主题、布局、菜单、设备类型等服务端菜单等配置项。
 */
import type { RouteRecordNormalized } from 'vue-router';

export interface AppState {
  theme: string;
  colorWeak: boolean;
  navbar: boolean;
  menu: boolean;
  topMenu: boolean;
  hideMenu: boolean;
  menuCollapse: boolean;
  footer: boolean;
  themeColor: string;
  menuWidth: number;
  globalSettings: boolean;
  device: string;
  tabBar: boolean;
  menuFromServer: boolean;
  serverMenu: RouteRecordNormalized[];
  [key: string]: unknown;
}
