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

/**
 * 角色属性枚举类
 * 定义了角色可更新的各种属性类型及其对应的位掩码值
 * 用于角色属性更新封包的编码和解码
 *
 * @author OdinMS Team
 */
public enum Stat {
    /** 肤色 */
    SKIN(0x1),
    /** 脸型 */
    FACE(0x2),
    /** 发型 */
    HAIR(0x4),
    /** 等级 */
    LEVEL(0x10),
    /** 职业 */
    JOB(0x20),
    /** 力量(STR) */
    STR(0x40),
    /** 敏捷(DEX) */
    DEX(0x80),
    /** 智力(INT) */
    INT(0x100),
    /** 运气(LUK) */
    LUK(0x200),
    /** 当前HP */
    HP(0x400),
    /** 最大HP */
    MAXHP(0x800),
    /** 当前MP */
    MP(0x1000),
    /** 最大MP */
    MAXMP(0x2000),
    /** 可用能力点(AP) */
    AVAILABLEAP(0x4000),
    /** 可用技能点(SP) */
    AVAILABLESP(0x8000),
    /** 经验值(EXP) */
    EXP(0x10000),
    /** 人气度 */
    FAME(0x20000),
    /** 金币 */
    MESO(0x40000),
    /** 宠物 */
    PET(0x180008),
    /** 转蛋经验 */
    GACHAEXP(0x200000);

    /** 属性对应的位掩码值 */
    private final int i;

    /**
     * 构造函数
     * @param i 属性的位掩码值
     */
    Stat(int i) {
        this.i = i;
    }

    /**
     * 获取属性的位掩码值
     * @return 位掩码值
     */
    public int getValue() {
        return i;
    }

    /**
     * 根据位掩码值获取对应的属性枚举
     * @param value 位掩码值
     * @return 对应的Stat枚举，如果未找到则返回null
     */
    public static Stat getByValue(int value) {
        for (Stat stat : Stat.values()) {
            if (stat.getValue() == value) {
                return stat;
            }
        }
        return null;
    }

    /**
     * 根据5字节编码获取对应的基础属性(STR/DEX/INT/LUK)
     * @param encoded 编码值
     * @return 对应的Stat枚举，如果不是基础属性则返回null
     */
    public static Stat getBy5ByteEncoding(int encoded) {
        return switch (encoded) {
            case 64 -> STR;
            case 128 -> DEX;
            case 256 -> INT;
            case 512 -> LUK;
            default -> null;
        };
    }

    /**
     * 根据属性名称字符串获取对应的属性枚举
     * @param type 属性名称字符串
     * @return 对应的Stat枚举，如果未找到则返回null
     */
    public static Stat getByString(String type) {
        for (Stat stat : Stat.values()) {
            if (stat.name().equals(type)) {
                return stat;
            }
        }
        return null;
    }
}
