/**
 * 在线玩家与资源发放 API
 * 提供在线角色列表查询、GM 发放物品/资源及装备初始信息查询接口。
 */
import axios from 'axios';

/** GM 发放资源表单（物品、属性等） */
export interface GiveForm {
  worldId?: number;
  playerId?: number;
  player?: string;
  type: number;
  id?: number;
  quantity?: number;
  rate?: number;
  str?: number;
  dex?: number;
  int?: number;
  luk?: number;
  hp?: number;
  mp?: number;
  pAtk?: number;
  mAtk?: number;
  pDef?: number;
  mDef?: number;
  acc?: number;
  avoid?: number;
  hands?: number;
  speed?: number;
  jump?: number;
  upgradeSlot?: number;
  expire?: number;
}

/** 分页查询在线玩家列表，POST /character/v1/online/list */
export function getPlayerList(
  pageNo: number,
  pageSize: number,
  id?: number,
  name?: string,
  map?: number
) {
  return axios.post('/character/v1/online/list', {
    pageNo,
    pageSize,
    id,
    name,
    map,
  });
}

/** GM 向玩家发放资源，POST /give/v1/resource */
export function givePlayerSrc(data: GiveForm) {
  return axios.post(`/give/v1/resource`, data);
}

/** 根据物品 ID 获取装备初始属性信息 */
export function getEquInitialInfo(id: number) {
  return axios.post(`/common/v1/getEquipmentInfoByItemId`, { id });
}
