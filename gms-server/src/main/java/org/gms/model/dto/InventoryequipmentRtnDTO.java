package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;

/**
 * 装备栏信息返回DTO
 * 用于返回装备的详细属性信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryequipmentRtnDTO {

    /**
     * 装备记录ID
     */
    private Long inventoryequipmentid;

    /**
     * 背包物品ID（外键）
     */
    private Long inventoryitemid;

    /**
     * 可升级次数
     */
    private Integer upgradeslots;

    /**
     * 装备等级
     */
    private Integer level;

    /**
     * 力量属性
     */
    private Integer str;

    /**
     * 敏捷属性
     */
    private Integer dex;

    /**
     * 智力属性
     */
    @Column("int")
    private Integer inte;

    /**
     * 运气属性
     */
    private Integer luk;

    /**
     * 生命值
     */
    private Integer hp;

    /**
     * 魔法值
     */
    private Integer mp;

    /**
     * 物理攻击力
     */
    private Integer watk;

    /**
     * 魔法攻击力
     */
    private Integer matk;

    /**
     * 物理防御力
     */
    private Integer wdef;

    /**
     * 魔法防御力
     */
    private Integer mdef;

    /**
     * 命中率
     */
    private Integer acc;

    /**
     * 回避率
     */
    private Integer avoid;

    /**
     * 攻击速度（手技）
     */
    private Integer hands;

    /**
     * 移动速度
     */
    private Integer speed;

    /**
     * 跳跃力
     */
    private Integer jump;

    /**
     * 是否锁定
     */
    private Integer locked;

    /**
     * 金锤子次数
     */
    private Long vicious;

    /**
     * 装备升级等级（潜能等级）
     */
    private Integer itemlevel;

    /**
     * 装备升级经验
     */
    private Long itemexp;
}
