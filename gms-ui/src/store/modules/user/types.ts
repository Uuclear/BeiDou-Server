/**
 * 用户状态类型定义
 * 描述游戏账户的完整字段及前端角色类型。
 */

/** 前端权限角色：空、通配、管理员、普通用户 */
export type RoleType = '' | '*' | 'admin' | 'user';

/** 当前登录用户的完整账户状态 */
export interface UserState {
  id?: number;
  name?: string;
  pin?: string;
  pic?: string;
  loggedin?: number;
  lastlogin?: string;
  createdat?: string;
  birthday?: string;
  banned?: boolean;
  banreason?: string;
  macs?: string;
  nxCredit?: number;
  maplePoint?: number;
  nxPrepaid?: number;
  characterslots?: number;
  gender?: number;
  tempban?: string;
  greason?: string;
  tos?: boolean;
  sitelogged?: string;
  webadmin?: boolean;
  nick?: string;
  mute?: boolean;
  email?: string;
  ip?: string;
  rewardpoints?: number;
  votepoints?: number;
  hwid?: string;
  language?: number;
  role: RoleType;
  avatar: undefined;
}
