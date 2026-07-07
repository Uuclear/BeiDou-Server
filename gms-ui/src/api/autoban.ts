/**
 * 自动封禁（Autoban）配置 API
 * 提供自动封禁规则列表查询及配置更新接口。
 */
import axios from 'axios';

/** 自动封禁规则配置项 */
export interface AutobanConfigResult {
  id: number;
  type: string;
  name: string;
  disabled: boolean;
  points: number | null;
  expireTimeSeconds: number | null;
  description: string;
  defaultPoints: number;
  defaultExpireTimeSeconds: number;
  changePoints: boolean;
  changeExpireTime: boolean;
}

/** 获取所有自动封禁配置列表 */
export function getAutobanConfigList() {
  return axios.get('/autoban/v1/getConfigList');
}

/** 更新自动封禁配置项 */
export function updateAutobanConfig(data: AutobanConfigResult) {
  return axios.post('/autoban/v1/updateConfig', data);
}
