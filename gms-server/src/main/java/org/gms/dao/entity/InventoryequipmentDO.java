package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 装备栏物品实体类，对应数据库表 inventoryequipment。
 * 存储装备栏物品详细属性。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventoryequipment")
public class InventoryequipmentDO implements Serializable  {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * inventoryequipmentid
     */
    private Long inventoryequipmentid;

    /**
     * 物品栏物品ID
     */
    private Long inventoryitemid;

    /**
     * upgradeslots
     */
    private Integer upgradeslots;

    /**
     * 等级
     */
    private Integer level;

    /**
     * str
     */
    private Integer str;

    /**
     * dex
     */
    private Integer dex;

    @Column("int")
    /**
     * inte
     */
    private Integer inte;

    /**
     * luk
     */
    private Integer luk;

    /**
     * 当前HP
     */
    private Integer hp;

    /**
     * 当前MP
     */
    private Integer mp;

    /**
     * watk
     */
    private Integer watk;

    /**
     * matk
     */
    private Integer matk;

    /**
     * wdef
     */
    private Integer wdef;

    /**
     * mdef
     */
    private Integer mdef;

    /**
     * acc
     */
    private Integer acc;

    /**
     * avoid
     */
    private Integer avoid;

    /**
     * hands
     */
    private Integer hands;

    /**
     * speed
     */
    private Integer speed;

    /**
     * jump
     */
    private Integer jump;

    /**
     * locked
     */
    private Integer locked;

    /**
     * vicious
     */
    private Integer vicious;

    /**
     * itemlevel
     */
    private Integer itemlevel;

    /**
     * itemexp
     */
    private Integer itemexp;

    /**
     * ringid
     */
    private Integer ringid;

}
