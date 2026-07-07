package org.gms.constants.inventory;

/**
 * 宠物装备各栏位索引的记录类型，对应装备、名牌、说话气泡、金币磁铁、物品 pouch、物品忽略等槽位。
 *
 * @param equip       宠物装备槽
 * @param nameTag     名牌槽
 * @param chatBalloon 说话气泡槽
 * @param mesoMagnet  金币磁铁槽
 * @param itemPouch   物品 pouch 槽
 * @param itemIgnore  物品忽略槽
 */
public record PetEquipSlot(short equip, short nameTag, short chatBalloon, short mesoMagnet, short itemPouch, short itemIgnore) {

}
