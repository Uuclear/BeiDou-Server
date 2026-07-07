/**
 * GM 命令管理 API
 * 提供命令列表查询、命令更新及地图/传送门/事件热重载接口。
 */
import axios from 'axios';

/** GM 命令数据结构 */
export interface CommandReq {
  id?: number;
  level?: number;
  levelList?: number[];
  syntax?: string;
  defaultLevel?: number;
  defaultLevelList?: number[];
  clazz?: string;
  description?: string;
  enabled?: boolean;
}

/** 从数据库分页查询 GM 命令列表 */
export function getCommandList(data: any) {
  return axios.post('/command/v1/getCommandListFromDB', data);
}

/** 更新 GM 命令配置 */
export function updateCommand(data: CommandReq) {
  return axios.post('/command/v1/updateCommand', data);
}

/** 通过 GM 命令热重载游戏事件脚本 */
export function reloadEventsByGMCommand() {
  return axios.get('/command/v1/reloadEventsByGMCommand');
}

/** 通过 GM 命令热重载传送门配置 */
export function reloadPortalsByGMCommand() {
  return axios.get('/command/v1/reloadPortalsByGMCommand');
}

/** 通过 GM 命令热重载地图配置 */
export function reloadMapsByGMCommand() {
  return axios.get('/command/v1/reloadMapsByGMCommand');
}
