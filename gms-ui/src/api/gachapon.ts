/**
 * 扭蛋（Gachapon）奖池管理 API
 * 提供奖池与奖品的查询、更新、删除接口。
 */
import axios from 'axios';
import {
  GachaponPoolState,
  GachaponRewardState,
} from '@/store/modules/gachapon/type';

/** 奖池搜索条件 */
export interface GachaponPoolSearchCondition {
  gachaponId?: number;
  pageNo: number;
  pageSize: number;
}

/** 分页查询扭蛋奖池列表 */
export function getPools(condition: GachaponPoolSearchCondition) {
  return axios.post('/gachapon/v1/getPools', condition);
}

/** 更新奖池配置 */
export function updatePool(data: GachaponPoolState) {
  return axios.post('/gachapon/v1/updatePool', data);
}

/** 删除奖池 */
export function deletePool(data: GachaponPoolState) {
  return axios.post('/gachapon/v1/deletePool', data);
}

/** 查询指定奖池下的奖品列表 */
export function getRewards(condition: GachaponPoolState) {
  return axios.post('/gachapon/v1/getRewards', condition);
}

/** 更新奖品配置 */
export function updateReward(data: GachaponRewardState) {
  return axios.post('/gachapon/v1/updateReward', data);
}

/** 删除奖品 */
export function deleteReward(data: GachaponRewardState) {
  return axios.post('/gachapon/v1/deleteReward', data);
}
