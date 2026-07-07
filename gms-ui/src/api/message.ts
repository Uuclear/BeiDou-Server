/**
 * 消息与聊天 Mock API
 * 提供站内消息列表查询、已读状态设置及聊天记录查询接口。
 */
import axios from 'axios';

/** 站内消息记录 */
export interface MessageRecord {
  id: number;
  type: string;
  title: string;
  subTitle: string;
  avatar?: string;
  content: string;
  time: string;
  status: 0 | 1;
  messageType?: number;
}

export type MessageListType = MessageRecord[];

/** 查询消息列表 */
export function queryMessageList() {
  return axios.post<MessageListType>('/api/message/list');
}

/** 批量标记消息已读请求体 */
interface MessageStatus {
  ids: number[];
}

/** 批量设置消息已读状态 */
export function setMessageStatus(data: MessageStatus) {
  return axios.post<MessageListType>('/api/message/read', data);
}

/** 聊天记录 */
export interface ChatRecord {
  id: number;
  username: string;
  content: string;
  time: string;
  isCollect: boolean;
}

/** 查询聊天记录列表 */
export function queryChatList() {
  return axios.post<ChatRecord[]>('/api/chat/list');
}
