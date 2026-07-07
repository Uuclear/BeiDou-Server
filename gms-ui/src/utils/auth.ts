/**
 * 认证 Token 工具
 * 基于 localStorage 管理 JWT Token 的存取与登录状态判断。
 */
const TOKEN_KEY = 'token';

/** 判断用户是否已登录（localStorage 中是否存在 Token） */
const isLogin = () => {
  return !!localStorage.getItem(TOKEN_KEY);
};

/** 获取当前 Token 字符串 */
const getToken = () => {
  return localStorage.getItem(TOKEN_KEY);
};

/** 持久化 Token 到 localStorage */
const setToken = (token: string) => {
  localStorage.setItem(TOKEN_KEY, token);
};

/** 清除 localStorage 中的 Token */
const clearToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};

export { isLogin, getToken, setToken, clearToken };
