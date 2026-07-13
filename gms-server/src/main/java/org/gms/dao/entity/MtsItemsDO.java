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
 * MTS物品实体类，对应数据库表 mts_items。
 * 存储MTS交易物品数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("mts_items")
public class MtsItemsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Long id;

    /**
     * tab
     */
    private Integer tab;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 物品ID
     */
    private Long itemid;

    /**
     * quantity
     */
    private Integer quantity;

    /**
     * seller
     */
    private Integer seller;

    /**
     * price
     */
    private Integer price;

    /**
     * bidIncre
     */
    private Integer bidIncre;

    /**
     * buyNow
     */
    private Integer buyNow;

    /**
     * position
     */
    private Integer position;

    /**
     * upgradeslots
     */
    private Integer upgradeslots;

    /**
     * 等级
     */
    private Integer level;

    /**
     * itemlevel
     */
    private Integer itemlevel;

    /**
     * itemexp
     */
    private Long itemexp;

    /**
     * ringid
     */
    private Integer ringid;

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
     * isequip
     */
    private Integer isequip;

    /**
     * owner
     */
    private String owner;

    /**
     * sellername
     */
    private String sellername;

    /**
     * sellEnds
     */
    private String sellEnds;

    /**
     * transfer
     */
    private Integer transfer;

    /**
     * vicious
     */
    private Long vicious;

    /**
     * flag
     */
    private Long flag;

    /**
     * expiration
     */
    private Long expiration;

    @Column("giftFrom")
    /**
     * giftFrom
     */
    private String giftFrom;

}
