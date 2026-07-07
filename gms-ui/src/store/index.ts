/**
 * Pinia 状态管理入口
 * 创建并导出 pinia 实例，同时导出各业务 store 的快捷引用。
 */
import { createPinia } from 'pinia';
import useAppStore from './modules/app';
import useUserStore from './modules/user';
import useTabBarStore from './modules/tab-bar';

const pinia = createPinia();

export { useAppStore, useUserStore, useTabBarStore };
export default pinia;
