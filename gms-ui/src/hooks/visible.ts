/**
 * 可见性状态组合式函数
 * 封装布尔 visible 状态及其设置、切换方法，常用于弹窗/抽屉显隐控制。
 */
import { ref } from 'vue';

export default function useVisible(initValue = false) {
  const visible = ref(initValue);
  const setVisible = (value: boolean) => {
    visible.value = value;
  };
  const toggle = () => {
    visible.value = !visible.value;
  };
  return {
    visible,
    setVisible,
    toggle,
  };
}
