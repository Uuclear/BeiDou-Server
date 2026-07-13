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
package org.gms.client;

import lombok.Getter;
import org.gms.util.I18nUtil;


/**
 * 职业枚举类
 * 定义了冒险岛游戏中所有可用的职业及其ID和名称
 * 包含冒险家、骑士团、战神、龙神等职业系列
 *
 * @author OdinMS Team
 */
public enum Job {
    /** 初心者/新手 */
    BEGINNER(0, I18nUtil.getMessage("job.name.0")),
    // 战士系
    /** 战士 */
    WARRIOR(100, I18nUtil.getMessage("job.name.100")),
    /** 剑客 */
    FIGHTER(110, I18nUtil.getMessage("job.name.110")),
    /** 勇士 */
    CRUSADER(111, I18nUtil.getMessage("job.name.111")),
    /** 英雄 */
    HERO(112, I18nUtil.getMessage("job.name.112")),
    /** 准骑士 */
    PAGE(120, I18nUtil.getMessage("job.name.120")),
    /** 骑士 */
    WHITEKNIGHT(121, I18nUtil.getMessage("job.name.121")),
    /** 圣骑士 */
    PALADIN(122,  I18nUtil.getMessage("job.name.122")),
    /** 枪战士 */
    SPEARMAN(130,  I18nUtil.getMessage("job.name.130")),
    /** 龙骑士 */
    DRAGONKNIGHT(131,  I18nUtil.getMessage("job.name.131")),
    /** 黑骑士 */
    DARKKNIGHT(132, I18nUtil.getMessage("job.name.132")),

    // 法师系
    /** 魔法师 */
    MAGICIAN(200, I18nUtil.getMessage("job.name.200")),
    /** 火毒法师 */
    FP_WIZARD(210, I18nUtil.getMessage("job.name.210")),
    /** 火毒巫师 */
    FP_MAGE(211, I18nUtil.getMessage("job.name.211")),
    /** 火毒魔导师 */
    FP_ARCHMAGE(212, I18nUtil.getMessage("job.name.212")),
    /** 冰雷法师 */
    IL_WIZARD(220, I18nUtil.getMessage("job.name.220")),
    /** 冰雷巫师 */
    IL_MAGE(221, I18nUtil.getMessage("job.name.221")),
    /** 冰雷魔导师 */
    IL_ARCHMAGE(222, I18nUtil.getMessage("job.name.222")),
    /** 牧师 */
    CLERIC(230, I18nUtil.getMessage("job.name.230")),
    /** 祭司 */
    PRIEST(231, I18nUtil.getMessage("job.name.231")),
    /** 主教 */
    BISHOP(232, I18nUtil.getMessage("job.name.232")),

    // 弓箭手系
    /** 弓箭手 */
    BOWMAN(300, I18nUtil.getMessage("job.name.300")),
    /** 猎人 */
    HUNTER(310, I18nUtil.getMessage("job.name.310")),
    /** 游侠 */
    RANGER(311, I18nUtil.getMessage("job.name.311")),
    /** 神射手 */
    BOWMASTER(312, I18nUtil.getMessage("job.name.312")),
    /** 弩弓手 */
    CROSSBOWMAN(320, I18nUtil.getMessage("job.name.320")),
    /** 游侠 */
    SNIPER(321, I18nUtil.getMessage("job.name.321")),
    /** 箭神 */
    MARKSMAN(322, I18nUtil.getMessage("job.name.322")),

    // 飞侠系
    /** 飞侠 */
    THIEF(400, I18nUtil.getMessage("job.name.400")),
    /** 刺客 */
    ASSASSIN(410,I18nUtil.getMessage("job.name.410")),
    /** 无影人 */
    HERMIT(411, I18nUtil.getMessage("job.name.411")),
    /** 隐士 */
    NIGHTLORD(412, I18nUtil.getMessage("job.name.412")),
    /** 侠客 */
    BANDIT(420, I18nUtil.getMessage("job.name.420")),
    /** 独行客 */
    CHIEFBANDIT(421, I18nUtil.getMessage("job.name.421")),
    /** 侠盗 */
    SHADOWER(422, I18nUtil.getMessage("job.name.422")),

    // 海盗系
    /** 海盗 */
    PIRATE(500, I18nUtil.getMessage("job.name.500")),
    /** 拳手 */
    BRAWLER(510, I18nUtil.getMessage("job.name.510")),
    /** 斗士 */
    MARAUDER(511, I18nUtil.getMessage("job.name.511")),
    /** 冲锋队长 */
    BUCCANEER(512, I18nUtil.getMessage("job.name.512")),
    /** 枪手 */
    GUNSLINGER(520, I18nUtil.getMessage("job.name.520")),
    /** 大副 */
    OUTLAW(521, I18nUtil.getMessage("job.name.521")),
    /** 船长 */
    CORSAIR(522, I18nUtil.getMessage("job.name.522")),

    /** 枫叶骑士团（管理员） */
    MAPLELEAF_BRIGADIER(800, I18nUtil.getMessage("job.name.800")),
    /** 管理员 */
    GM(900, I18nUtil.getMessage("job.name.900")),
    /** 超级管理员 */
    SUPERGM(910, I18nUtil.getMessage("job.name.910")),

    // 骑士团系
    /** 贵族/初心者 */
    NOBLESSE(1000, I18nUtil.getMessage("job.name.1000")),
    /** 魂骑士1转 */
    DAWNWARRIOR1(1100, I18nUtil.getMessage("job.name.1100")),
    /** 魂骑士2转 */
    DAWNWARRIOR2(1110, I18nUtil.getMessage("job.name.1110")),
    /** 魂骑士3转 */
    DAWNWARRIOR3(1111, I18nUtil.getMessage("job.name.1111")),
    /** 魂骑士4转 */
    DAWNWARRIOR4(1112, I18nUtil.getMessage("job.name.1112")),
    /** 炎术士1转 */
    BLAZEWIZARD1(1200, I18nUtil.getMessage("job.name.1200")),
    /** 炎术士2转 */
    BLAZEWIZARD2(1210, I18nUtil.getMessage("job.name.1210")),
    /** 炎术士3转 */
    BLAZEWIZARD3(1211,I18nUtil.getMessage("job.name.1211")),
    /** 炎术士4转 */
    BLAZEWIZARD4(1212,I18nUtil.getMessage("job.name.1212")),
    /** 风灵使者1转 */
    WINDARCHER1(1300,I18nUtil.getMessage("job.name.1300")),
    /** 风灵使者2转 */
    WINDARCHER2(1310, I18nUtil.getMessage("job.name.1310")),
    /** 风灵使者3转 */
    WINDARCHER3(1311, I18nUtil.getMessage("job.name.1311")),
    /** 风灵使者4转 */
    WINDARCHER4(1312, I18nUtil.getMessage("job.name.1312")),
    /** 夜行者1转 */
    NIGHTWALKER1(1400,I18nUtil.getMessage("job.name.1400")),
    /** 夜行者2转 */
    NIGHTWALKER2(1410,I18nUtil.getMessage("job.name.1410")),
    /** 夜行者3转 */
    NIGHTWALKER3(1411,I18nUtil.getMessage("job.name.1411")),
    /** 夜行者4转 */
    NIGHTWALKER4(1412,I18nUtil.getMessage("job.name.1412")),
    /** 奇袭者1转 */
    THUNDERBREAKER1(1500,I18nUtil.getMessage("job.name.1500")),
    /** 奇袭者2转 */
    THUNDERBREAKER2(1510,I18nUtil.getMessage("job.name.1510")),
    /** 奇袭者3转 */
    THUNDERBREAKER3(1511,I18nUtil.getMessage("job.name.1511")),
    /** 奇袭者4转 */
    THUNDERBREAKER4(1512,I18nUtil.getMessage("job.name.1512")),

    // 英雄系
    /** 战神/传说 */
    LEGEND(2000, I18nUtil.getMessage("job.name.2000")),
    /** 龙神 */
    EVAN(2001, I18nUtil.getMessage("job.name.2001")),
    /** 战神1转 */
    ARAN1(2100, I18nUtil.getMessage("job.name.2100")),
    /** 战神2转 */
    ARAN2(2110, I18nUtil.getMessage("job.name.2110")),
    /** 战神3转 */
    ARAN3(2111, I18nUtil.getMessage("job.name.2111")),
    /** 战神4转 */
    ARAN4(2112, I18nUtil.getMessage("job.name.2112")),

    /** 龙神1转 */
    EVAN1(2200,I18nUtil.getMessage("job.name.2200")),
    /** 龙神2转 */
    EVAN2(2210, I18nUtil.getMessage("job.name.2210")),
    /** 龙神3转 */
    EVAN3(2211, I18nUtil.getMessage("job.name.2211")),
    /** 龙神4转 */
    EVAN4(2212, I18nUtil.getMessage("job.name.2212")),
    /** 龙神5转 */
    EVAN5(2213, I18nUtil.getMessage("job.name.2213")),
    /** 龙神6转 */
    EVAN6(2214, I18nUtil.getMessage("job.name.2214")),
    /** 龙神7转 */
    EVAN7(2215, I18nUtil.getMessage("job.name.2215")),
    /** 龙神8转 */
    EVAN8(2216, I18nUtil.getMessage("job.name.2216")),
    /** 龙神9转 */
    EVAN9(2217, I18nUtil.getMessage("job.name.2217")),
    /** 龙神10转 */
    EVAN10(2218, I18nUtil.getMessage("job.name.2218"));

    /** 职业ID */
    @Getter
    private final int id;
    /** 职业名称（支持国际化） */
    @Getter
    private final String name;

    /** 最大职业ID */
    final static int maxId = 22;

    /**
     * 构造函数
     * @param id 职业ID
     * @param name 职业名称
     */
    Job(int id, String name) {
        this.id = id;
        this.name = name;
    }


    /**
     * 获取最大职业ID
     * @return 最大职业ID
     */
    public static int getMax() {
        return maxId;
    }

    /**
     * 根据ID获取职业枚举
     * @param id 职业ID
     * @return 对应的Job枚举，未找到则返回BEGINNER
     */
    public static Job getById(int id) {
        for (Job l : Job.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return BEGINNER;
    }

    /**
     * 根据5字节编码获取职业（用于角色创建封包）
     * @param encoded 编码值
     * @return 对应的Job枚举
     */
    public static Job getBy5ByteEncoding(int encoded) {
        return switch (encoded) {
            case 2 -> WARRIOR;
            case 4 -> MAGICIAN;
            case 8 -> BOWMAN;
            case 16 -> THIEF;
            case 32 -> PIRATE;
            case 1024 -> NOBLESSE;
            case 2048 -> DAWNWARRIOR1;
            case 4096 -> BLAZEWIZARD1;
            case 8192 -> WINDARCHER1;
            case 16384 -> NIGHTWALKER1;
            case 32768 -> THUNDERBREAKER1;
            default -> BEGINNER;
        };
    }

    /**
     * 判断当前职业是否属于某个基础职业分支
     * @param basejob 基础职业
     * @return 是否属于该职业分支
     */
    public boolean isA(Job basejob) {
        int basebranch = basejob.getId() / 10;
        return (getId() / 10 == basebranch && getId() >= basejob.getId()) || (basebranch % 10 == 0 && getId() / 100 == basejob.getId() / 100);
    }

    /**
     * 获取职业分支类型
     * @return 职业分支：0=新手, 1=战士, 2=法师, 3=弓手, 4=飞侠, 5=海盗
     */
    public int getJobNiche() {
        return (id / 100) % 10;
    }

    /**
     * 根据职业ID和选项获取职业风格类型（用于伤害计算等）
     * @param jobid 职业ID
     * @param opt 选项（力量型/敏捷型）
     * @return 对应的基础职业类型
     */
    public static Job getJobStyleInternal(int jobid, byte opt) {
        int jobtype = jobid / 100;

        if (jobtype == WARRIOR.getId() / 100 || jobtype == DAWNWARRIOR1.getId() / 100 || jobtype == ARAN1.getId() / 100) {
            return WARRIOR;
        } else if (jobtype == MAGICIAN.getId() / 100 || jobtype == BLAZEWIZARD1.getId() / 100 || jobtype == EVAN1.getId() / 100) {
            return MAGICIAN;
        } else if (jobtype == BOWMAN.getId() / 100 || jobtype == WINDARCHER1.getId() / 100) {
            if (jobid / 10 == CROSSBOWMAN.getId() / 10) {
                return CROSSBOWMAN;
            } else {
                return BOWMAN;
            }
        } else if (jobtype == THIEF.getId() / 100 || jobtype == NIGHTWALKER1.getId() / 100) {
            return THIEF;
        } else if (jobtype == PIRATE.getId() / 100 || jobtype == THUNDERBREAKER1.getId() / 100) {
            if (opt == (byte) 0x80) {
                return BRAWLER;
            } else {
                return GUNSLINGER;
            }
        }

        return BEGINNER;
    }
}
