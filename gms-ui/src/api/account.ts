/**
 * 游戏账户管理 API
 * 提供账户列表查询、注册、GM 修改、封禁/解封及登录状态重置接口。
 */
import axios from 'axios';
import { isValidString } from '@/utils/stringUtils';
import { PageState } from '@/store/page';

/** 新账户注册表单 */
export interface RegisterForm {
  name?: string;
  password?: string;
  checkPassword?: string;
  birthday?: string;
  language?: number;
}

/** GM 修改账户信息表单 */
export interface GMUpdateForm {
  newPwd?: string;
  newPwdCheck?: string;
  pin?: string;
  pic?: string;
  birthday?: string;
  nxCredit?: number;
  maplePoint?: number;
  nxPrepaid?: number;
  characterslots?: number;
  gender?: number;
  webadmin?: boolean;
  nick?: string;
  mute?: boolean;
  email?: string;
  rewardpoints?: number;
  votepoints?: number;
  language?: number;
}

/**
 * 分页查询账户列表
 * 支持按 ID、用户名、最后登录时间、创建时间筛选
 */
export function getAccountList(
  page: number,
  size: number,
  id?: number,
  name?: string,
  lastLoginStart?: string,
  lastLoginEnd?: string,
  createdAtStart?: string,
  createdAtEnd?: string
) {
  let url = `/account/v1?page=${page}&size=${size}`;
  if (id !== undefined && id > 0) url += `&id=${id}`;
  if (isValidString(name)) url += `&name=${name}`;
  if (isValidString(lastLoginStart)) url += `&lastLoginStart=${lastLoginStart}`;
  if (isValidString(lastLoginEnd)) url += `&lastLoginEnd=${lastLoginEnd}`;
  if (isValidString(createdAtStart)) url += `&createdAtStart=${createdAtStart}`;
  if (isValidString(createdAtEnd)) url += `&createdAtEnd=${createdAtEnd}`;
  return axios.get<PageState>(url);
}

/** 注册新账户，POST /account/v1 */
export function addAccount(data: RegisterForm) {
  return axios.post('/account/v1', data);
}

/** GM 修改指定账户信息，PUT /account/v1/:id */
export function updateAccountByGM(id: number, data: GMUpdateForm) {
  return axios.put(`/account/v1/${id}`, data);
}

/** 删除账户，DELETE /account/v1/:id */
export function deleteAccount(id: number) {
  return axios.delete(`/account/v1/${id}`);
}

/** 封禁账户，PUT /account/v1/:id/ban */
export function banAccount(id: number, reason?: string) {
  return axios.put(`/account/v1/${id}/ban`, { reason });
}

/** 解封账户，PUT /account/v1/:id/unban */
export function unbanAccount(id: number) {
  return axios.put(`/account/v1/${id}/unban`);
}

/** 重置账户在线登录状态，PUT /account/v1/:id/reset/logged */
export function resetLoggedIn(id: number) {
  return axios.put(`/account/v1/${id}/reset/logged`);
}
