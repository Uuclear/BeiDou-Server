/**
 * 路由常量
 * 白名单、默认首页、重定向路由名等全局路由配置常量。
 */

/** 无需服务端菜单校验即可访问的路由白名单 */
export const WHITE_LIST = [
  { name: 'notFound', children: [] },
  { name: 'login', children: [] },
];

export const NOT_FOUND = {
  name: 'notFound',
};

export const REDIRECT_ROUTE_NAME = 'Redirect';

export const DEFAULT_ROUTE_NAME = 'Workplace';

/** 默认固定在工作台的首个标签页 */
export const DEFAULT_ROUTE = {
  title: 'menu.dashboard.workplace',
  name: DEFAULT_ROUTE_NAME,
  fullPath: '/dashboard/workplace',
};
