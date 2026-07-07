/**
 * 游戏服务器管理 API
 * 提供服务器状态查询、启动、停止、重启、关闭及版本信息接口。
 */
import axios from 'axios';

/** 查询服务器是否在线 */
export function getServerStatus() {
  return axios.get<boolean>('/server/v1/online');
}

/** 启动游戏服务器 */
export function startServer() {
  return axios.get('/server/v1/startServer');
}

/** 停止服务器参数（倒计时、公告消息等） */
interface StopServerParams {
  minutes: number;
  shutdownMsg: string;
  showServerMsg: boolean;
  showCenterMsg: boolean;
  showChatMsg: boolean;
}

/** 带倒计时和公告消息的优雅停服 */
export function stopServer(params: StopServerParams) {
  return axios.post('/server/v1/stopServerWithMsgAndInternal', params);
}

/** 重启游戏服务器 */
export function restartServer() {
  return axios.get('/server/v1/restartServer');
}

/** 立即关闭游戏服务器 */
export function shutdown() {
  return axios.get('/server/v1/shutdown');
}

/** 获取服务器版本信息 */
export function getVersion() {
  return axios.get('/server/v1/version');
}
