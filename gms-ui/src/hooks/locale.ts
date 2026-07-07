/**
 * 国际化语言切换组合式函数
 * 读写 vue-i18n locale，并持久化到 localStorage。
 */
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { Message } from '@arco-design/web-vue';

export default function useLocale() {
  const i18 = useI18n();
  /** 当前语言代码，如 zh-CN / en-US */
  const currentLocale = computed(() => {
    return i18.locale.value;
  });
  /** 切换语言并保存到 localStorage */
  const changeLocale = (value: string) => {
    if (i18.locale.value === value) {
      return;
    }
    i18.locale.value = value;
    localStorage.setItem('arco-locale', value);
    Message.success(i18.t('message.switch.success'));
  };
  return {
    currentLocale,
    changeLocale,
  };
}
