/**
 * 游戏文件树管理 API
 * 提供服务端文件目录树浏览、文件读取及写入接口。
 */
import axios from 'axios';

/** 文件树浏览请求（指定当前目录 key） */
export interface FileTreeForm {
  currentKey: string;
}

/** 文件读取请求 */
export interface ReadForm {
  currentKey: string;
  title: string;
}

/** 文件写入请求 */
export interface WriteForm {
  currentKey: string;
  title: string;
  content: string;
}

/** 获取文件目录树 */
export function treeFile(data: FileTreeForm) {
  return axios.post('/file/v1/tree', data);
}

/** 读取指定文件内容 */
export function readFile(data: ReadForm) {
  return axios.post('/file/v1/tree/read', data);
}

/** 写入/保存文件内容 */
export function writeFile(data: WriteForm) {
  return axios.post('/file/v1/tree/write', data);
}
