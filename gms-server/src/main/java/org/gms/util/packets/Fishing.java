/*
    This file is part of the HeavenMS Maple Story Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.util.packets;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.util.NumberTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

import java.util.Calendar;

/**
 * 钓鱼系统工具类
 * <p>
 * 实现自定义钓鱼系统，基于时间模式计算钓鱼成功率，
 * 支持三种奖励：金币、经验、道具。钓鱼成功率受日期、时间和鱼饵等级影响。
 * </p>
 *
 * @author FateJiki (RaGeZONE)
 * @author Ronan - timing pattern
 */
public class Fishing {
    private static final Logger log = LoggerFactory.getLogger(Fishing.class);

    /**
     * 计算钓鱼概率基础值
     * <p>
     * 使用正弦和余弦函数生成波动的概率值，营造随时间变化的钓鱼体验。
     * </p>
     *
     * @param x 输入参数（日期或时间值）
     * @return 概率基础值
     */
    private static double getFishingLikelihood(int x) {
        return 50.0 + 7.0 * (7.0 * Math.sin(x)) * (Math.cos(Math.pow(x, 0.777)));
    }

    /**
     * 获取当前时间的钓鱼概率因子
     * <p>
     * 基于一年中的第几天和当前时间计算两个概率因子，
     * 用于综合判断是否成功钓到鱼。
     * </p>
     *
     * @return 包含年份概率和时间概率的double数组
     */
    public static double[] fetchFishingLikelihood() {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        double yearLikelihood = getFishingLikelihood(dayOfYear);
        double timeLikelihood = getFishingLikelihood(hours + minutes + seconds);

        return new double[]{yearLikelihood, timeLikelihood};
    }

    /**
     * 判断是否命中钓鱼成功时间
     * <p>
     * 综合年份概率(23%)、时间概率(77%)和鱼饵加成计算总得分，
     * 得分超过57.777则判定为钓鱼成功。
     * </p>
     *
     * @param chr            玩家角色
     * @param baitLevel      鱼饵等级
     * @param yearLikelihood 年份概率因子
     * @param timeLikelihood 时间概率因子
     * @return 如果钓鱼成功返回true，否则返回false
     */
    private static boolean hitFishingTime(Character chr, int baitLevel, double yearLikelihood, double timeLikelihood) {
        double baitLikelihood = 0.0002 * chr.getWorldServer().getFishingRate() * baitLevel;

        if (GameConfig.getServerBoolean("use_debug") && chr.isGM()) {
            chr.dropMessage(5, "----- FISHING RESULT -----");
            chr.dropMessage(5, "Likelihoods - Year: " + yearLikelihood + " Time: " + timeLikelihood + " Meso: " + baitLikelihood);
            chr.dropMessage(5, "Score rolls - Year: " + (0.23 * yearLikelihood) + " Time: " + (0.77 * timeLikelihood) + " Meso: " + baitLikelihood);
        }

        return (0.23 * yearLikelihood) + (0.77 * timeLikelihood) + (baitLikelihood) > 57.777;
    }

    /**
     * 执行钓鱼操作
     * <p>
     * 检查钓鱼条件（在线状态、存活状态、钓鱼区域、等级要求），
     * 根据成功率判定结果，并发放相应奖励（金币/经验/道具）。
     * </p>
     *
     * @param chr            玩家角色
     * @param baitLevel      鱼饵等级
     * @param yearLikelihood 年份概率因子
     * @param timeLikelihood 时间概率因子
     */
    public static void doFishing(Character chr, int baitLevel, double yearLikelihood, double timeLikelihood) {
        if (!chr.isLoggedInWorld() || !chr.isAlive()) {
            return;
        }

        if (!MapId.isFishingArea(chr.getMapId())) {
            chr.dropMessage("You are not in a fishing area!");
            return;
        }

        if (chr.getLevel() < 30) {
            chr.dropMessage(5, "You must be above level 30 to fish!");
            return;
        }

        String fishingEffect;
        if (!hitFishingTime(chr, baitLevel, yearLikelihood, timeLikelihood)) {
            fishingEffect = "Effect/BasicEff.img/Catch/Fail";
        } else {
            String rewardStr = "";
            fishingEffect = "Effect/BasicEff.img/Catch/Success";

            int rand = (int) (3.0 * Math.random());
            switch (rand) {
                case 0:
                    int mesoAward = NumberTool.doubleToInt((1400.0 * Math.random() + 1201.0) * chr.getMesoRate()) + (15 * chr.getLevel() / 5);
                    chr.gainMeso(mesoAward, true, true, true);
                    rewardStr = mesoAward + " mesos.";
                    break;
                case 1:
                    int expAward = NumberTool.doubleToInt((645.0 * Math.random() + 620.0) * chr.getExpRate()) + (15 * chr.getLevel() / 4);
                    chr.gainExp(expAward, true, true);
                    rewardStr = expAward + " EXP.";
                    break;
                case 2:
                    int itemid = getRandomItem();
                    rewardStr = "a(n) " + ItemInformationProvider.getInstance().getName(itemid) + ".";

                    if (chr.canHold(itemid)) {
                        chr.getAbstractPlayerInteraction().gainItem(itemid, true);
                    } else {
                        chr.showHint("Couldn't catch a(n) #r" + ItemInformationProvider.getInstance().getName(itemid) + "#k due to #e#b" + ItemConstants.getInventoryType(itemid) + "#k#n inventory limit.");
                        rewardStr += ".. but has goofed up due to full inventory.";
                    }
                    break;
            }

            chr.getMap().dropMessage(6, chr.getName() + " found " + rewardStr);
        }

        chr.sendPacket(PacketCreator.showInfo(fishingEffect));
        chr.getMap().broadcastMessage(chr, PacketCreator.showForeignInfo(chr.getId(), fishingEffect), false);
    }

    /**
     * 获取随机钓鱼奖励道具
     * <p>
     * 按照稀有度随机选择道具：
     * <ul>
     *   <li>普通物品(75%概率)：常见消耗品</li>
     *   <li> uncommon物品(4%概率)：装备和卷轴</li>
     *   <li>稀有物品(21%概率)：高级装备和稀有道具</li>
     * </ul>
     * </p>
     *
     * @return 随机道具ID
     */
    public static int getRandomItem() {
        int rand = (int) (100.0 * Math.random());
        int[] commons = {1002851, 2002020, 2002020, ItemId.MANA_ELIXIR, 2000018, 2002018, 2002024, 2002027, 2002027, 2000018, 2000018, 2000018, 2000018, 2002030, 2002018, 2000016};
        int[] uncommons = {1000025, 1002662, 1002812, 1002850, 1002881, 1002880, 1012072, 4020009, 2043220, 2043022, 2040543, 2044420, 2040943, 2043713, 2044220, 2044120, 2040429, 2043220, 2040943};
        int[] rares = {1002859, 1002553, 1002762, 1002763, 1002764, 1002765, 1002766, 1002663, 1002788, 1002949, 2049100, 2340000, 2040822, 2040822, 2040822, 2040822};

        if (rand >= 25) {
            return commons[(int) (commons.length * Math.random())];
        } else if (rand <= 7 && rand >= 4) {
            return uncommons[(int) (uncommons.length * Math.random())];
        } else {
            return rares[(int) (rares.length * Math.random())];
        }
    }

    /**
     * 调试用方法：计算一年中钓鱼成功率的统计数据
     * <p>
     * 遍历全年每一天的每一秒，统计钓鱼成功次数，输出最小/最大成功率。
     * </p>
     */
    private static void debugFishingLikelihood() {
        long[] a = new long[365], b = new long[365];
        long hits = 0, hits10 = 0, total = 0;

        for (int i = 0; i < 365; i++) {
            double yearLikelihood = getFishingLikelihood(i);

            int dayHits = 0, dayHits10 = 0;
            for (int k = 0; k < 24; k++) {
                for (int l = 0; l < 60; l++) {
                    for (int m = 0; m < 60; m++) {
                        double timeLikelihood = getFishingLikelihood(k + l + m);

                        if ((0.23 * yearLikelihood) + (0.77 * timeLikelihood) > 57.777) {
                            hits++;
                            dayHits++;
                        }

                        if ((0.23 * yearLikelihood) + (0.77 * timeLikelihood) + 10.0 > 57.777) {
                            hits10++;
                            dayHits10++;
                        }

                        total++;
                    }
                }
            }

            a[i] = dayHits;
            b[i] = dayHits10;
        }

        long maxhit = 0, minhit = Long.MAX_VALUE;
        for (int i = 0; i < 365; i++) {
            if (maxhit < a[i]) {
                maxhit = a[i];
            }

            if (minhit > a[i]) {
                minhit = a[i];
            }
        }

        long maxhit10 = 0, minhit10 = Long.MAX_VALUE;
        for (int i = 0; i < 365; i++) {
            if (maxhit10 < b[i]) {
                maxhit10 = b[i];
            }

            if (minhit10 > b[i]) {
                minhit10 = b[i];
            }
        }

        log.debug("Diary   min {} max {}", minhit, maxhit);
        log.debug("Diary10 min {} max {}", minhit10, maxhit10);
        log.debug("Hits: {}, Hits10: {}, Total: {} -- %1000 {}, +10 %1000: {}", hits, hits10, total, (hits * 1000 / total), (hits10 * 1000 / total));
    }
} 
