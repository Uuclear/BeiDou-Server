/**
 * NPC 商店管理 API
 * 提供商店列表、商品列表查询及商品的增删改接口。
 */
import axios from 'axios';
import { NpcShopItemState } from '@/store/modules/npcShop/type';

/** NPC 商店查询筛选条件 */
export interface getShopFilter {
  pageNo?: number;
  pageSize?: number;
  onlyTotal: boolean;
  notPage: boolean;
  shopId?: number;
  npcId?: number;
  npcName?: string;
  itemId?: number;
  itemName?: string;
}

/** 查询商店列表 */
export function getShopList(data: getShopFilter) {
  return axios.post('/shop/v1/getShopList', data);
}

/** 查询商店商品列表 */
export function getShopItemList(data: getShopFilter) {
  return axios.post('/shop/v1/getShopItemList', data);
}

/** 删除商店商品 */
export function deleteShopItem(id: number) {
  return axios.delete(`/shop/v1/deleteShopItem/${id}`);
}

/** 新增商店商品 */
export function addShopItem(data: NpcShopItemState) {
  return axios.put(`/shop/v1/addShopItem`, data);
}

/** 更新商店商品 */
export function updateShopItem(data: NpcShopItemState) {
  return axios.post(`/shop/v1/updateShopItem`, data);
}
