/**
 * NPC 商店相关类型定义
 */

/** NPC 商店基本信息 */
export interface NpcShopState {
  shopId?: number;
  npcId?: number;
  npcName?: string;
}

/** NPC 商店中的单个商品 */
export interface NpcShopItemState {
  id?: number;
  shopId?: number;
  itemId?: number;
  price?: number;
  pitch?: number;
  position?: number;
  itemName?: string;
  itemDesc?: string;
}
