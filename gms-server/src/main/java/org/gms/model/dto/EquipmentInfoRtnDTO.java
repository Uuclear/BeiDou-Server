package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 装备信息返回DTO
 * 用于返回装备的详细属性信息
 */
@Setter
@Getter
public class EquipmentInfoRtnDTO {
//    private Integer worldId;
//    private Integer playerId;
//    private String player;
//    private Byte type;
//    private Integer id;
//    private Integer quantity;
//    private Integer rate;
    /**
     * 力量属性加成
     */
    private Short str;

    /**
     * 敏捷属性加成
     */
    private Short dex;

    /**
     * 智力属性加成
     */
    @JsonProperty("int")
    private Short _int;

    /**
     * 运气属性加成
     */
    private Short luk;

    /**
     * 生命值加成
     */
    private Short hp;

    /**
     * 魔法值加成
     */
    private Short mp;

    /**
     * 物理攻击力
     */
    private Short pAtk;

    /**
     * 魔法攻击力
     */
    private Short mAtk;

    /**
     * 物理防御力
     */
    private Short pDef;

    /**
     * 魔法防御力
     */
    private Short mDef;

    /**
     * 命中率
     */
    private Short acc;

    /**
     * 回避率
     */
    private Short avoid;

    /**
     * 攻击速度
     */
    private Short hands;

    /**
     * 移动速度
     */
    private Short speed;

    /**
     * 跳跃力
     */
    private Short jump;

    /**
     * 可升级次数
     */
    private Byte upgradeSlot;

    /**
     * 过期时间戳
     */
    private Long expire;
}
