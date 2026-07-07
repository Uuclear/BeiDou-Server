/**
 * 应用入口文件
 * 负责创建 Vue 应用实例，注册 Arco Design、路由、状态管理、国际化、
 * 全局组件、自定义指令，并配置 Monaco Editor 资源路径后挂载到 DOM。
 */
import { createApp } from 'vue';
import ArcoVue from '@arco-design/web-vue';
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import globalComponents from '@/components';
import { loader } from '@guolao/vue-monaco-editor';
import router from './router';
import store from './store';
import i18n from './locale';
import directive from './directive';
import './mock';
import App from './App.vue';
// Styles are imported via arco-plugin. See config/plugin/arcoStyleImport.ts in the directory for details
// 样式通过 arco-plugin 插件导入。详见目录文件 config/plugin/arcoStyleImport.ts
// https://arco.design/docs/designlab/use-theme-package
import '@/assets/style/global.less';
import '@/api/interceptor';

const app = createApp(App);

app.use(ArcoVue, {});
app.use(ArcoVueIcon);

app.use(router);
app.use(store);
app.use(i18n);
app.use(globalComponents);
app.use(directive);

loader.config({
  paths: {
    vs: 'https://unpkg.com/monaco-editor@0.52.2/min/vs',
    // vs: 'https://cdn.jsdelivr.net/npm/monaco-editor@0.52.2/min/vs',
    // vs: 'https://unpkg.com/monaco-editor@0.52.2/min/vs',
    // vs: 'https://cdnjs.cloudflare.com/ajax/libs/monaco-editor/0.52.2/min/vs',
    // vs: 'https://cdn.bootcdn.net/ajax/libs/monaco-editor/0.52.2/min/vs',
  },
});

app.mount('#app');
