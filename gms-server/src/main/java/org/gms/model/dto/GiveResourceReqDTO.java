package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 发放物品资源请求DTO
 * 用于GM给玩家发放物品/装备/资源的请求参数
 */
@Setter
@Getter
public class GiveResourceReqDTO {
    /**
     * 世界ID
     */
    private Integer worldId;

    /**
     * 玩家ID
     */
    private Integer playerId;

    /**
     * 玩家名称
     */
    private String player;

    /**
     * 资源类型
     */
    private Byte type;

    /**
     * 物品ID
     */
    private Integer id;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 强化/卷轴成功率
     */
    private Float rate;

    /**
     * 力量属性
     */
    private Short str;

    /**
     * 敏捷属性
     */
    private Short dex;

    /**
     * 智力属性
     */
    @JsonProperty("int")
    private Short _int;

    /**
     * 运气属性
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
    @JsonProperty("pAtk")
    private Short pAtk;

    /**
     * 魔法攻击力
     */
    @JsonProperty("mAtk")
    private Short mAtk;

    /**
     * 物理防御力
     */
    @JsonProperty("pDef")
    private Short pDef;

    /**
     * 魔法防御力
     */
    @JsonProperty("mDef")
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
     * 攻击速度（手技）
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
