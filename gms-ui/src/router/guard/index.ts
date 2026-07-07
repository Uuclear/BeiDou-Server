/**
 * 路由守卫注册入口
 * 依次挂载页面级、登录信息、权限三类 beforeEach 守卫。
 */
import type { Router } from 'vue-router';
import { setRouteEmitter } from '@/utils/route-listener';
import setupUserLoginInfoGuard from './userLoginInfo';
import setupPermissionGuard from './permission';

/** 路由变化时通过 mitt 发布事件，供菜单等组件订阅 */
function setupPageGuard(router: Router) {
  router.beforeEach(async (to) => {
    // emit route change
    setRouteEmitter(to);
  });
}

export default function createRouteGuard(router: Router) {
  setupPageGuard(router);
  setupUserLoginInfoGuard(router);
  setupPermissionGuard(router);
}
