/**
 * 用户认证与账户信息 API
 * 提供登录、登出、获取用户信息、菜单列表及 Token 刷新接口。
 */
import axios from 'axios';
import type { RouteRecordNormalized } from 'vue-router';
import { UserState } from '@/store/modules/user/types';

/** 登录请求参数 */
export interface LoginData {
  username: string;
  password: string;
}

/** 通用提交请求体（含 requestId 包装前的原始 data） */
export interface SubmitBody {
  requestId: string;
  data: any;
}

/** 登录成功响应，包含 JWT Token */
export interface LoginRes {
  token: string;
}

/** 用户登录，POST /auth/v1/login */
export function login(data: LoginData) {
  return axios.post<LoginRes>('/auth/v1/login', data);
}

/** 用户登出，DELETE /auth/v1/logout */
export function logout() {
  return axios.delete<LoginRes>('/auth/v1/logout');
}

/** 获取当前登录用户信息，GET /account/v1/info */
export function getUserInfo() {
  return axios.get<UserState>('/account/v1/info');
}

/** 获取服务端动态菜单配置，GET /account/v1/menu */
export function getMenuList() {
  return axios.get<RouteRecordNormalized[]>('/account/v1/menu');
}

/** 刷新 JWT Token，GET /auth/v1/refreshToken */
export function refreshToken() {
  return axios.get<LoginRes>('/auth/v1/refreshToken');
}
