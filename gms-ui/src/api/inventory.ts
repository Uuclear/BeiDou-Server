/**
 * 角色背包（库存）管理 API
 * 提供背包类型、角色列表、物品列表查询及物品更新/删除接口。
 */
import axios from 'axios';
import { InventoryState } from '@/store/modules/inventory/type';

/** 背包查询条件 */
export interface InventoryCondition {
  inventoryType?: number;
  characterId?: number;
  characterName?: string;
  accountId?: number;
  pageNo: number;
  pageSize: number;
}

/** 获取所有背包类型列表 */
export function getInventoryTypeList() {
  return axios.get('/inventory/v1/getInventoryTypeList');
}

/** 根据条件分页查询角色列表 */
export function getCharacterList(condition: InventoryCondition) {
  return axios.post('/inventory/v1/getCharacterList', condition);
}

/** 根据条件分页查询背包物品列表 */
export function getInventoryList(condition: InventoryCondition) {
  return axios.post('/inventory/v1/getInventoryList', condition);
}

/** 更新背包中的物品数据 */
export function updateInventory(data: InventoryState) {
  return axios.post('/inventory/v1/updateInventory', data);
}

/** 从背包中删除物品 */
export function deleteInventory(data: InventoryState) {
  return axios.post('/inventory/v1/deleteInventory', data);
}
