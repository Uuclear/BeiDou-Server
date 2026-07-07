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
package org.gms.server.gachapon;

import lombok.Getter;
import org.gms.client.Character;
import org.gms.constants.id.NpcId;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Randomizer;

/**
 * 扭蛋机主类（单例），按 NPC 类型抽取稀有度分层奖品。
 */
public class Gachapon {
    private static final Logger log = LoggerFactory.getLogger(Gachapon.class);
    private static final Gachapon instance = new Gachapon();

    /**
     * 获取单例实例。
     * @return Gachapon 类型结果
     */
    public static Gachapon getInstance() {
        return instance;
    }

    public enum GachaponType {

        GLOBAL(-1, -1, -1, -1, new Global()),
        HENESYS(NpcId.GACHAPON_HENESYS, 90, 8, 2, new Henesys()),
        ELLINIA(NpcId.GACHAPON_ELLINIA, 90, 8, 2, new Ellinia()),
        PERION(NpcId.GACHAPON_PERION, 90, 8, 2, new Perion()),
        KERNING_CITY(NpcId.GACHAPON_KERNING, 90, 8, 2, new KerningCity()),
        SLEEPYWOOD(NpcId.GACHAPON_SLEEPYWOOD, 90, 8, 2, new Sleepywood()),
        MUSHROOM_SHRINE(NpcId.GACHAPON_MUSHROOM_SHRINE, 90, 8, 2, new MushroomShrine()),
        SHOWA_SPA_MALE(NpcId.GACHAPON_SHOWA_MALE, 90, 8, 2, new ShowaSpaMale()),
        SHOWA_SPA_FEMALE(NpcId.GACHAPON_SHOWA_FEMALE, 90, 8, 2, new ShowaSpaFemale()),
        LUDIBRIUM(NpcId.GACHAPON_LUDIBRIUM, 90, 8, 2, new Ludibrium()),
        NEW_LEAF_CITY(NpcId.GACHAPON_NLC, 90, 8, 2, new NewLeafCity()),
        EL_NATH(NpcId.GACHAPON_EL_NATH, 90, 8, 2, new ElNath()),
        NAUTILUS_HARBOR(NpcId.GACHAPON_NAUTILUS, 90, 8, 2, new NautilusHarbor());

        private static final GachaponType[] values = GachaponType.values();

        private final GachaponItems gachapon;
        @Getter
        private final int npcId;
        private final int common;
        private final int uncommon;
        private final int rare;

        GachaponType(int npcid, int c, int u, int r, GachaponItems g) {
            this.npcId = npcid;
            this.gachapon = g;
            this.common = c;
            this.uncommon = u;
            this.rare = r;
        }

        private int getTier() {
            int chance = Randomizer.nextInt(common + uncommon + rare) + 1;
            if (chance > common + uncommon) {
                return 2; //Rare
            } else if (chance > common) {
                return 1; //Uncommon
            } else {
                return 0; //Common
            }
        }

        /**
         * 获取物品。
         * @param tier 稀有度层级
         * @return int[] 类型结果
         */
        public int[] getItems(int tier) {
            return gachapon.getItems(tier);
        }

        /**
         * 获取物品。
         * @param tier 稀有度层级
         * @return int 类型结果
         */
        public int getItem(int tier) {
            int[] gacha = getItems(tier);
            int[] global = GLOBAL.getItems(tier);
            int chance = Randomizer.nextInt(gacha.length + global.length);
            return chance < gacha.length ? gacha[chance] : global[chance - gacha.length];
        }

        /**
         * 获取按NPCID。
         * @param npcId NPC ID
         * @return GachaponType 类型结果
         */
        public static GachaponType getByNpcId(int npcId) {
            for (GachaponType gacha : values) {
                if (npcId == gacha.npcId) {
                    return gacha;
                }
            }
            return null;
        }

        /**
         * 获取战利品、Names。
         * @return String[] 类型结果
         */
        public static String[] getLootNames() {
            return new String[]{
                    I18nUtil.getMessage("GachaCommand.message2"),
                    I18nUtil.getMessage("GachaCommand.message3"),
                    I18nUtil.getMessage("GachaCommand.message4"),
                    I18nUtil.getMessage("GachaCommand.message5"),
                    I18nUtil.getMessage("GachaCommand.message6"),
                    I18nUtil.getMessage("GachaCommand.message7"),
                    I18nUtil.getMessage("GachaCommand.message8"),
                    I18nUtil.getMessage("GachaCommand.message9"),
                    I18nUtil.getMessage("GachaCommand.message10"),
                    I18nUtil.getMessage("GachaCommand.message11")
            };
        }

        /**
         * 获取战利品Ids。
         * @return int[] 类型结果
         */
        public static int[] getLootIds() {
            return new int[]{
                    NpcId.GACHAPON_HENESYS,
                    NpcId.GACHAPON_ELLINIA,
                    NpcId.GACHAPON_PERION,
                    NpcId.GACHAPON_KERNING,
                    NpcId.GACHAPON_SLEEPYWOOD,
                    NpcId.GACHAPON_MUSHROOM_SHRINE,
                    NpcId.GACHAPON_SHOWA_MALE,
                    NpcId.GACHAPON_SHOWA_FEMALE,
                    NpcId.GACHAPON_NLC,
                    NpcId.GACHAPON_NAUTILUS
            };
        }
    }

    /**
     * 执行 process 操作。
     * @param npcId NPC ID
     * @return GachaponItem 类型结果
     */
    public GachaponItem process(int npcId) {
        GachaponType gacha = GachaponType.getByNpcId(npcId);
        int tier = gacha.getTier();
        int item = gacha.getItem(tier);
        return new GachaponItem(tier, item);
    }

    public static class GachaponItem {
        private final int id;
        private final int tier;

        /**
         * 执行 扭蛋物品 操作。
         * @param t 稀有度层级
         * @param i 物品 ID
         * @return GachaponItem 类型结果
         */
        public GachaponItem(int t, int i) {
            id = i;
            tier = t;
        }

        /**
         * 获取层级。
         * @return int 类型结果
         */
        public int getTier() {
            return tier;
        }

        /**
         * 获取ID。
         * @return int 类型结果
         */
        public int getId() {
            return id;
        }
    }

    /**
     * 记录日志。
     * @param player 玩家
     * @param itemId 物品 ID
     * @param map 地图名称
     */
    public static void log(Character player, int itemId, String map) {
        String itemName = ItemInformationProvider.getInstance().getName(itemId);
        log.info(I18nUtil.getLogMessage("Gachapon.log.info"), player.getName(), itemName, itemId, map);
    }
}
