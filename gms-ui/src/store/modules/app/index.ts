/**
 * 应用全局设置 Store
 * 管理主题、布局、设备类型、服务端菜单等应用级状态。
 */
import { defineStore } from 'pinia';
import { Notification } from '@arco-design/web-vue';
import type { NotificationReturn } from '@arco-design/web-vue/es/notification/interface';
import type { RouteRecordNormalized } from 'vue-router';
import defaultSettings from '@/config/settings.json';
import { getMenuList } from '@/api/user';
import { AppState } from './types';

const useAppStore = defineStore('app', {
  state: (): AppState => ({ ...defaultSettings }),

  getters: {
    /** 返回当前应用设置的浅拷贝 */
    appCurrentSetting(state: AppState): AppState {
      return { ...state };
    },
    /** 当前设备类型：mobile / desktop */
    appDevice(state: AppState) {
      return state.device;
    },
    /** 从服务端获取的动态菜单路由列表 */
    appAsyncMenus(state: AppState): RouteRecordNormalized[] {
      return state.serverMenu as unknown as RouteRecordNormalized[];
    },
  },

  actions: {
    /** 局部更新应用设置 */
    updateSettings(partial: Partial<AppState>) {
      // @ts-ignore-next-line
      this.$patch(partial);
    },

    /** 切换明暗主题，同步设置 body 的 arco-theme 属性 */
    toggleTheme(dark: boolean) {
      if (dark) {
        this.theme = 'dark';
        document.body.setAttribute('arco-theme', 'dark');
      } else {
        this.theme = 'light';
        document.body.removeAttribute('arco-theme');
      }
    },
    /** 切换设备类型标识 */
    toggleDevice(device: string) {
      this.device = device;
    },
    /** 控制侧边菜单是否隐藏 */
    toggleMenu(value: boolean) {
      this.hideMenu = value;
    },
    /** 从服务端拉取动态菜单配置，带加载/成功/失败通知 */
    async fetchServerMenuConfig() {
      let notifyInstance: NotificationReturn | null = null;
      try {
        notifyInstance = Notification.info({
          id: 'menuNotice', // Keep the instance id the same
          content: 'loading',
          closable: true,
        });
        const { data } = await getMenuList();
        this.serverMenu = data;
        notifyInstance = Notification.success({
          id: 'menuNotice',
          content: 'success',
          closable: true,
        });
      } catch (error) {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        notifyInstance = Notification.error({
          id: 'menuNotice',
          content: 'error',
          closable: true,
        });
      }
    },
    /** 清空服务端菜单缓存 */
    clearServerMenu() {
      this.serverMenu = [];
    },
  },
});

export default useAppStore;
