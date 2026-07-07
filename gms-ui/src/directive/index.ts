/**
 * 自定义指令注册入口
 * 全局注册 v-permission 权限指令。
 */
import { App } from 'vue';
import permission from './permission';

export default {
  install(Vue: App) {
    Vue.directive('permission', permission);
  },
};
