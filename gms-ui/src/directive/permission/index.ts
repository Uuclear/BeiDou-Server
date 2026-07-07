/**
 * v-permission 权限指令
 * 根据用户角色控制 DOM 元素显示：无权限时从父节点移除该元素。
 */
import { DirectiveBinding } from 'vue';
import { useUserStore } from '@/store';

/** 校验绑定角色数组是否包含当前用户角色 */
function checkPermission(el: HTMLElement, binding: DirectiveBinding) {
  const { value } = binding;
  const userStore = useUserStore();
  const { role } = userStore;

  if (Array.isArray(value)) {
    if (value.length > 0) {
      const permissionValues = value;

      const hasPermission = permissionValues.includes(role);
      if (!hasPermission && el.parentNode) {
        el.parentNode.removeChild(el);
      }
    }
  } else {
    throw new Error(`need roles! Like v-permission="['admin','user']"`);
  }
}

export default {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding);
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    checkPermission(el, binding);
  },
};
