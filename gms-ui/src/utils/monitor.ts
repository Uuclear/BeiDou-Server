/**
 * 前端运行时错误监控
 * 将 Vue 组件运行时错误上报到指定后端地址（需配置 baseUrl）。
 */
import { App, ComponentPublicInstance } from 'vue';
import axios from 'axios';

/** 注册 Vue 全局 errorHandler，将错误信息 POST 到监控服务 */
export default function handleError(Vue: App, baseUrl: string) {
  if (!baseUrl) {
    return;
  }
  Vue.config.errorHandler = (
    err: unknown,
    instance: ComponentPublicInstance | null,
    info: string
  ) => {
    // send error info
    axios.post(`${baseUrl}/report-error`, {
      err,
      instance,
      info,
      // location: window.location.href,
      // message: err.message,
      // stack: err.stack,
      // browserInfo: getBrowserInfo(),
      // user info
      // dom info
      // url info
      // ...
    });
  };
}
