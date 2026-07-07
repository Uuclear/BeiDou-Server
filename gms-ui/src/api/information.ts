/**
 * 游戏数据信息检索 API
 * 提供按类型和关键字搜索游戏内物品、NPC、地图等信息的接口。
 */
import axios from 'axios';

/** 信息检索条件 */
export interface InformationSearch {
  types: [];
  filter: string;
}

/** 信息检索结果项 */
export interface InformationResult {
  type: string;
  id: number;
  name: string;
  desc: string;
}

/** 按类型和关键字搜索游戏数据信息 */
export function informationSearch(condition: InformationSearch) {
  return axios.post('/common/v1/informationSearch', condition);
}
