/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.constants.inventory;

import org.gms.client.inventory.InventoryType;
import org.gms.config.GameConfig;
import org.gms.constants.id.ItemId;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 物品系统通用常量及判定方法，涵盖背包类型、堆叠规则、现金物品、装备标志位等。
 *
 * @author Jay Estrella
 * @author Ronan
 */
public final class ItemConstants {
    protected static Map<Integer, InventoryType> inventoryTypeCache = new HashMap<>();

    public final static short LOCK = 0x01;
    public final static short SPIKES = 0x02;
    public final static short KARMA_USE = 0x02;
    public final static short COLD = 0x04;
    public final static short UNTRADEABLE = 0x08;
    public final static short KARMA_EQP = 0x10;
    public final static short SANDBOX = 0x40;             // let 0x40 until it's proven something uses this
    public final static short PET_COME = 0x80;
    public final static short ACCOUNT_SHARING = 0x100;
    public final static short MERGE_UNTRADEABLE = 0x200;

    public final static Set<Integer> permanentItemids = new HashSet<>();

    static {
        // i ain't going to open one gigantic itemid cache just for 4 perma itemids, no way!
        for (int petItemId : ItemId.getPermaPets()) {
            permanentItemids.add(petItemId);
        }
    }

    /**
     * 将整型标志转换为物品标志位短整型。
     */
    public static int getFlagByInt(int type) {
        if (type == 128) {
            return PET_COME;
        } else if (type == 256) {
            return ACCOUNT_SHARING;
        }
        return 0;
    }

    /**
     * 判断物品是否为飞镖类消耗品。
     */
    public static boolean isThrowingStar(int itemId) {
        return itemId / 10000 == 207;
    }

    /**
     * 判断物品是否为子弹类消耗品。
     */
    public static boolean isBullet(int itemId) {
        return itemId / 10000 == 233;
    }

    /**
     * 判断物品是否为药水。
     */
    public static boolean isPotion(int itemId) {
        return itemId / 1000 == 2000;
    }

    /**
     * 判断物品是否为食物。
     */
    public static boolean isFood(int itemId) {
        int useType = itemId / 1000;
        return useType == 2022 || useType == 2010 || useType == 2020;
    }

    /**
     * 判断Consumable相关条件是否成立。
     */
    public static boolean isConsumable(int itemId) {
        return isPotion(itemId) || isFood(itemId);
    }

    /**
     * 判断Rechargeable相关条件是否成立。
     */
    public static boolean isRechargeable(int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    /**
     * 判断ArrowForCrossBow相关条件是否成立。
     */
    public static boolean isArrowForCrossBow(int itemId) {
        return itemId / 1000 == 2061;
    }

    /**
     * 判断ArrowForBow相关条件是否成立。
     */
    public static boolean isArrowForBow(int itemId) {
        return itemId / 1000 == 2060;
    }

    /**
     * 判断Arrow相关条件是否成立。
     */
    public static boolean isArrow(int itemId) {
        return isArrowForBow(itemId) || isArrowForCrossBow(itemId);
    }

    /**
     * 判断物品是否为宠物。
     */
    public static boolean isPet(int itemId) {
        return itemId / 1000 == 5000;
    }

    /**
     * 判断ExpirablePet相关条件是否成立。
     */
    public static boolean isExpirablePet(int itemId) {
        return GameConfig.getServerBoolean("use_erase_pet_on_expiration") || itemId == ItemId.PET_SNAIL;
    }

    /**
     * 判断PermanentItem相关条件是否成立。
     */
    public static boolean isPermanentItem(int itemId) {
        return permanentItemids.contains(itemId);
    }

    /**
     * 判断NewYearCardEtc相关条件是否成立。
     */
    public static boolean isNewYearCardEtc(int itemId) {
        return itemId / 10000 == 430;
    }

    /**
     * 判断NewYearCardUse相关条件是否成立。
     */
    public static boolean isNewYearCardUse(int itemId) {
        return itemId / 10000 == 216;
    }

    /**
     * 判断Accessory相关条件是否成立。
     */
    public static boolean isAccessory(int itemId) {
        return itemId >= 1110000 && itemId < 1140000;
    }

    /**
     * 判断Taming相关条件是否成立。
     */
    public static boolean isTaming(int itemId) {
        int itemType = itemId / 1000;
        return itemType == 1902 || itemType == 1912;
    }

    /**
     * 判断物品是否为回城卷轴。
     */
    public static boolean isTownScroll(int itemId) {
        return itemId >= 2030000 && itemId < ItemId.ANTI_BANISH_SCROLL;
    }

    /**
     * 判断AntibanishScroll相关条件是否成立。
     */
    public static boolean isAntibanishScroll(int itemId) {
        return itemId == ItemId.ANTI_BANISH_SCROLL;
    }

    /**
     * 判断物品是否为白衣卷轴。
     */
    public static boolean isCleanSlate(int scrollId) {
        return scrollId > 2048999 && scrollId < 2049004;
    }

    /**
     * 判断物品是否为属性卷轴。
     */
    public static boolean isModifierScroll(int scrollId) {
        return scrollId == ItemId.SPIKES_SCROLL || scrollId == ItemId.COLD_PROTECTION_SCROLl;
    }

    /**
     * 判断FlagModifier相关条件是否成立。
     */
    public static boolean isFlagModifier(int scrollId, short flag) {
        if (scrollId == ItemId.COLD_PROTECTION_SCROLl && ((flag & ItemConstants.COLD) == ItemConstants.COLD)) {
            return true;
        }
        return scrollId == ItemId.SPIKES_SCROLL && ((flag & ItemConstants.SPIKES) == ItemConstants.SPIKES);
    }

    /**
     * 判断物品是否为混沌卷轴。
     */
    public static boolean isChaosScroll(int scrollId) {
        return scrollId >= 2049100 && scrollId <= 2049103;
    }

    /**
     * 判断物品 ID 是否为倍率券。
     */
    public static boolean isRateCoupon(int itemId) {
        int itemType = itemId / 1000;
        return itemType == 5211 || itemType == 5360;
    }

    /**
     * 判断ExpCoupon相关条件是否成立。
     */
    public static boolean isExpCoupon(int couponId) {
        return couponId / 1000 == 5211;
    }

    /**
     * 判断PartyItem相关条件是否成立。
     */
    public static boolean isPartyItem(int itemId) {
        return itemId >= 2022430 && itemId <= 2022433 || itemId >= 2022160 && itemId <= 2022163;
    }

    /**
     * 判断HiredMerchant相关条件是否成立。
     */
    public static boolean isHiredMerchant(int itemId) {
        return itemId / 10000 == 503;
    }

    /**
     * 判断PlayerShop相关条件是否成立。
     */
    public static boolean isPlayerShop(int itemId) {
        return itemId / 10000 == 514;
    }

    /**
     * 根据物品 ID 推断背包类型。
     */
    public static InventoryType getInventoryType(final int itemId) {
        if (inventoryTypeCache.containsKey(itemId)) {
            return inventoryTypeCache.get(itemId);
        }

        InventoryType ret = InventoryType.UNDEFINED;

        final byte type = (byte) (itemId / 1000000);
        if (type >= 1 && type <= 5) {
            ret = InventoryType.getByType(type);
        }

        inventoryTypeCache.put(itemId, ret);
        return ret;
    }

    /**
     * 判断物品是否为 Maker 试剂。
     */
    public static boolean isMakerReagent(int itemId) {
        return itemId / 10000 == 425;
    }

    /**
     * 判断物品是否为套服。
     */
    public static boolean isOverall(int itemId) {
        return itemId / 10000 == 105;
    }

    /**
     * 判断CashStore相关条件是否成立。
     */
    public static boolean isCashStore(int itemId) {
        int itemType = itemId / 10000;
        return itemType == 503 || itemType == 514;
    }

    /**
     * 判断MapleLife相关条件是否成立。
     */
    public static boolean isMapleLife(int itemId) {
        int itemType = itemId / 10000;
        return itemType == 543 && itemId != 5430000;
    }

    /**
     * 判断物品是否为武器。
     */
    public static boolean isWeapon(int itemId) {
        return itemId >= 1302000 && itemId < 1493000;
    }

    /**
     * 判断Equipment相关条件是否成立。
     */
    public static boolean isEquipment(int itemId) {
        return itemId < 2000000 && itemId != 0;
    }

    /**
     * 判断FishingChair相关条件是否成立。
     */
    public static boolean isFishingChair(int itemId) {
        return itemId == ItemId.FISHING_CHAIR;
    }

    /**
     * 判断Medal相关条件是否成立。
     */
    public static boolean isMedal(int itemId) {
        return itemId >= 1140000 && itemId < 1143000;
    }

    /**
     * 判断Face相关条件是否成立。
     */
    public static boolean isFace(int itemId) {
        int itemType = itemId / 10000;
        return itemType == 2 || itemType == 5;
    }

    /**
     * 判断Hair相关条件是否成立。
     */
    public static boolean isHair(int itemId) {
        int itemType = itemId / 10000;
        return itemType == 3 || itemType == 4 || itemType == 6;
    }

    /**
     * 判断NewCharDefaultFace相关条件是否成立。
     */
    public static boolean isNewCharDefaultFace(int job, int gender, int faceId) {
        if (job == 0 || job == 1) {
            return switch (gender) {
                case 0 -> faceId == 20000 || faceId == 20001 || faceId == 20002;
                case 1 -> faceId == 21000 || faceId == 21001 || faceId == 21002;
                default -> false;
            };
        } else if (job == 2) {
            return switch (gender) {
                case 0 -> faceId == 20100 || faceId == 20401 || faceId == 20402;
                case 1 -> faceId == 21700 || faceId == 21201 || faceId == 21002;
                default -> false;
            };
        } else {
            return false;
        }
    }

    /**
     * 判断NewCharDefaultHair相关条件是否成立。
     */
    public static boolean isNewCharDefaultHair(int gender, int hairId) {
        return switch (gender) {
            case 0 -> hairId == 30000 || hairId == 30020 || hairId == 30030;
            case 1 -> hairId == 31000 || hairId == 31040 || hairId == 31050;
            default -> false;
        };
    }

    /**
     * 判断NewCharDefaultHairColor相关条件是否成立。
     */
    public static boolean isNewCharDefaultHairColor(int hairColor) {
        return hairColor == 0 || hairColor == 2 || hairColor == 3 || hairColor == 7;
    }

    /**
     * 判断NewCharDefaultSkinColor相关条件是否成立。
     */
    public static boolean isNewCharDefaultSkinColor(int skinColor) {
        return skinColor >= 0 && skinColor < 4;
    }

    /**
     * 判断NewCharDefaultTop相关条件是否成立。
     */
    public static boolean isNewCharDefaultTop(int job, int gender, int topId) {
        if (job == 0 || job == 1) {
            return switch (gender) {
                case 0 -> topId == 1040002 || topId == 1040006 || topId == 1040010;
                case 1 -> topId == 1041002 || topId == 1041006 || topId == 1041010 || topId == 1041011;
                default -> false;
            };
        } else if (job == 2) {
            return topId == 1042167;
        } else {
            return false;
        }
    }

    /**
     * 判断NewCharDefaultBottom相关条件是否成立。
     */
    public static boolean isNewCharDefaultBottom(int job, int gender, int bottomId) {
        if (job == 0 || job == 1) {
            return switch (gender) {
                case 0 -> bottomId == 1060002 || bottomId == 1060006;
                case 1 -> bottomId == 1061002 || bottomId == 1061008;
                default -> false;
            };
        } else if (job == 2) {
            return bottomId == 1062115;
        } else {
            return false;
        }
    }

    /**
     * 判断NewCharDefaultShoes相关条件是否成立。
     */
    public static boolean isNewCharDefaultShoes(int job, int shoesId) {
        if (job == 0 || job == 1) {
            return shoesId == 1072001 || shoesId == 1072005 || shoesId == 1072037 || shoesId == 1072038;
        } else if (job == 2) {
            return shoesId == 1072383;
        } else {
            return false;
        }
    }

    /**
     * 判断NewCharDefaultWeapon相关条件是否成立。
     */
    public static boolean isNewCharDefaultWeapon(int job, int weaponId) {
        if (job == 0 || job == 1) {
            return weaponId == 1302000 || weaponId == 1322005 || weaponId == 1312004;
        } else if (job == 2) {
            return weaponId == 1442079;
        } else {
            return false;
        }
    }

    /**
     * notValidHairColor 相关查询或判定。
     */
    public static boolean notValidHairColor(int hairColor) {
        return hairColor > 7 || hairColor < 0;
    }

    // is_correct_bodypart
    public final static short Pet0Equip = -114;
    public final static short Pet1Equip = -130;
    public final static short Pet2Equip = -138;

    public final static short Pet0NameTag = -121;
    public final static short Pet1NameTag = -131;
    public final static short Pet2NameTag = -139;

    public final static short Pet0ChatBalloon = -129;
    public final static short Pet1ChatBalloon = -132;
    public final static short Pet2ChatBalloon = -140;

    // itemId = 1812000
    public final static short Pet0MesoMagnet = -123;
    public final static short Pet1MesoMagnet = -134;
    public final static short Pet2MesoMagnet = -142;

    // itemId = 1812001
    public final static short Pet0ItemPouch = -122;
    public final static short Pet1ItemPouch = -133;
    public final static short Pet2ItemPouch = -141;

    // itemId = 1812007
    public final static short Pet0ItemIgnore = -146;
    public final static short Pet1ItemIgnore = -147;
    public final static short Pet2ItemIgnore = -148;

    public static final List<PetEquipSlot> PET_EQUIP_SLOTS = Collections.unmodifiableList(Arrays.asList(
        new PetEquipSlot(Pet0Equip, Pet0NameTag, Pet0ChatBalloon, Pet0MesoMagnet, Pet0ItemPouch, Pet0ItemIgnore),
        new PetEquipSlot(Pet1Equip, Pet1NameTag, Pet1ChatBalloon, Pet1MesoMagnet, Pet1ItemPouch, Pet1ItemIgnore),
        new PetEquipSlot(Pet2Equip, Pet2NameTag, Pet2ChatBalloon, Pet2MesoMagnet, Pet2ItemPouch, Pet2ItemIgnore)
    ));

    public static final List<Short> PETS_NAME_TAG = Collections.unmodifiableList(Arrays.asList(Pet0NameTag, Pet1NameTag, Pet2NameTag));

    /**
     * 判断ValidPetIndex相关条件是否成立。
     */
    public static boolean isValidPetIndex(byte petIndex) {
        return petIndex >= 0 && petIndex < 3;
    }
}
