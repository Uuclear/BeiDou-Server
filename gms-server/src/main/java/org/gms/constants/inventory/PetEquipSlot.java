package org.gms.constants.inventory;

/**
 * 宠物装备槽位记录类
 * <p>
 * 定义宠物装备的各个槽位位置，包括装备、名称标签、聊天气球、
 * 金币磁铁、物品口袋、物品忽略等槽位。
 * </p>
 *
 * @param equip      宠物装备槽位
 * @param nameTag    名称标签槽位
 * @param chatBalloon 聊天气球槽位
 * @param mesoMagnet 金币磁铁槽位
 * @param itemPouch  物品口袋槽位
 * @param itemIgnore 物品忽略槽位
 * @author GMS Team
 * @since 1.0.0
 */
public record PetEquipSlot(short equip, short nameTag, short chatBalloon, short mesoMagnet, short itemPouch, short itemIgnore) {

}
