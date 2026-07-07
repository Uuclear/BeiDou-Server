/**
 * 怪物掉落数据类型定义
 */

/** 单条掉落记录（怪物掉落或全局掉落） */
export interface DropState {
  id?: number;
  dropperId?: number;
  dropperName?: string;
  continent?: number;
  itemId?: number;
  itemName?: string;
  minimumQuantity?: number;
  maximumQuantity?: number;
  questId?: number;
  questName?: string;
  chance?: number;
  comments?: string;
}
