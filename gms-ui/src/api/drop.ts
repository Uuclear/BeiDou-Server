/**
 * 怪物掉落与全局掉落管理 API
 * 提供普通掉落和全局掉落的查询、新增、更新、删除接口。
 */
import axios from 'axios';
import { DropState } from '@/store/modules/drop/type';

/** 掉落数据查询条件 */
export interface DropConditionState {
  dropperId?: number;
  dropperName?: string;
  continent?: number;
  itemId?: number;
  itemName?: string;
  questId?: number;
  pageNo?: number;
  pageSize?: number;
  onlyTotal?: boolean;
  notPage?: boolean;
}

/** 查询普通掉落列表 */
export function getDrop(data: DropConditionState) {
  return axios.post('/drop/v1/getDropList', data);
}

/** 更新普通掉落数据 */
export function updateDrop(data: DropState) {
  return axios.post('/drop/v1/updateDropData', data);
}

/** 新增普通掉落数据 */
export function insertDrop(data: DropState) {
  return axios.put('/drop/v1/addDropData', data);
}

/** 删除普通掉落数据 */
export function deleteDrop(data: DropState) {
  return axios.delete(`/drop/v1/deleteDropData/${data.id}`);
}

/** 查询全局掉落列表 */
export function getGlobalDrop(data: DropConditionState) {
  return axios.post('/drop/v1/getGlobalDropList', data);
}

/** 更新全局掉落数据 */
export function updateGlobalDrop(data: DropState) {
  return axios.post('/drop/v1/updateGlobalDropData', data);
}

/** 新增全局掉落数据 */
export function insertGlobalDrop(data: DropState) {
  return axios.put('/drop/v1/addGlobalDropData', data);
}

/** 删除全局掉落数据 */
export function deleteGlobalDrop(data: DropState) {
  return axios.delete(`/drop/v1/deleteGlobalDropData/${data.id}`);
}
