/**
 * 主题组合式函数
 * 根据 app store 中的 theme 字段判断当前是否为暗色主题。
 */
import { computed } from 'vue';
import { useAppStore } from '@/store';

export default function useThemes() {
  const appStore = useAppStore();
  /** 当前是否为暗色主题 */
  const isDark = computed(() => {
    return appStore.theme === 'dark';
  });
  return {
    isDark,
  };
}
