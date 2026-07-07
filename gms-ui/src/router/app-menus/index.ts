/**
 * 客户端菜单数据
 * 合并业务路由与外链路由，导出供侧栏菜单渲染的精简路由结构。
 */
import { appRoutes, appExternalRoutes } from '../routes';

const mixinRoutes = [...appRoutes, ...appExternalRoutes];

const appClientMenus = mixinRoutes.map((el) => {
  const { name, path, meta, redirect, children } = el;
  return {
    name,
    path,
    meta,
    redirect,
    children,
  };
});

export default appClientMenus;
