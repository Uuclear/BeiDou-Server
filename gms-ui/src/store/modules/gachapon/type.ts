/**
 * 扭蛋（Gachapon）相关类型定义
 */

/** 扭蛋奖池配置 */
export interface GachaponPoolState {
  id?: number;
  name?: string;
  gachaponId?: number;
  weight?: number;
  isPublic?: boolean;
  prob?: number;
  startTime?: number;
  endTime?: number;
  notification?: boolean;
  realProb?: number;
  comment?: string;
}

/** 奖池中的单个奖品 */
export interface GachaponRewardState {
  id?: number;
  poolId?: number;
  itemId?: number;
  quantity?: number;
  createTime?: number;
  comment?: string;
}
