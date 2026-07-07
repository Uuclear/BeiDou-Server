/**
 * 游戏账户数据类型定义
 * 用于账户列表展示，字段与数据库 accounts 表对应。
 */

/** 游戏账户完整信息 */
export interface AccountState {
  id: number;
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
}
