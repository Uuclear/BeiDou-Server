/**
 * 国际化（i18n）配置入口
 * 创建 vue-i18n 实例，加载中英文语言包，并从 localStorage 恢复用户语言偏好。
 */
import { createI18n } from 'vue-i18n';
import en from './en-US';
import cn from './zh-CN';

export const LOCALE_OPTIONS = [
  { label: '中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' },
];
const defaultLocale = localStorage.getItem('arco-locale') || 'zh-CN';

const i18n = createI18n({
  locale: defaultLocale,
  fallbackLocale: 'en-US',
  legacy: false,
  allowComposition: true,
  messages: {
    'en-US': en,
    'zh-CN': cn,
  },
});

export default i18n;
