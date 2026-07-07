/**
 * 背包（库存）相关类型定义
 * 描述背包类型、装备属性及背包物品数据结构。
 */

/** 背包类型（装备栏、消耗栏等） */
export interface InventoryTypeState {
  inventoryType: number;
  name: string;
}

/** 装备物品的详细属性 */
export interface InventoryEquipmentState {
  id: number;
  inventoryItemId: number;
  upgradeSlots: number;
  level: number;
  attStr: number;
  attDex: number;
  attInt: number;
  attLuk: number;
  hp: number;
  mp: number;
  patk: number;
  matk: number;
  pdef: number;
  mdef: number;
  acc: number;
  avoid: number;
  hands: number;
  speed: number;
  jump: number;
  locked: number;
  vicious: number;
  itemLevel: number;
  itemExp: number;
  ringId: number;
}

/** 背包中的单个物品记录 */
export interface InventoryState {
  id?: number;
  characterId?: number;
  itemId?: number;
  itemType?: number;
  inventoryType?: number;
  position?: number;
  quantity?: number;
  owner?: string;
  petId?: number;
  flag?: number;
  expiration?: number;
  giftFrom?: string;
  online?: boolean;
  equipment?: boolean;
  inventoryEquipment: InventoryEquipmentState;
}
