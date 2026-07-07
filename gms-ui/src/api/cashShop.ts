/**
 * 商城（Cash Shop）商品管理 API
 * 提供分类列表、商品查询、上下架及批量上架接口。
 */
import axios from 'axios';
import { cashShopState } from '@/store/modules/cashShop/type';

/** 按分类查询商品的条件 */
export interface conditionState {
  id: number;
  subId: number;
  onSale?: number;
  pageNo: number;
  itemId?: number;
}

/** 商城商品编辑表单 */
export interface cashShopFormState {
  sn: number;
  itemId: number;
  count?: number;
  price?: number;
  bonus?: number;
  priority?: number;
  period?: number;
  maplePoint?: number;
  meso?: number;
  forPremiumUser?: number;
  commodityGender?: number;
  onSale?: number;
  clz?: number;
  limit?: number;
  pbCash?: number;
  pbPoint?: number;
  pbGift?: number;
  packageSn?: number;
}

/** 批量上架操作表单 */
export interface batchFormState {
  data: cashShopState[];
  type: string;
  value?: number;
}

/** 获取所有商城分类列表 */
export function getAllCategoryList() {
  return axios.get('/cashShop/v1/getAllCategoryList');
}

/** 按分类分页查询商品 */
export function getCommodityByCategory(condition: conditionState) {
  return axios.post('/cashShop/v1/getCommodityByCategory', condition);
}

/** 商品上架 */
export function onSale(data: cashShopFormState) {
  return axios.post('/cashShop/v1/onSale', data);
}

/** 商品下架 */
export function offSale(data: cashShopFormState) {
  return axios.post('/cashShop/v1/offSale', data);
}

/** 批量上架商品 */
export function batchOnSale(data: batchFormState) {
  return axios.post('/cashShop/v1/batchOnSale', data);
}
