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
 * 物品栏物品实体类，对应数据库表 inventoryitems。
 * 存储所有物品栏物品。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventoryitems")
public class InventoryitemsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 物品栏物品ID
     */
    private Long inventoryitemid;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * 账号ID
     */
    private Integer accountid;

    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * inventorytype
     */
    private Integer inventorytype;

    /**
     * position
     */
    private Integer position;

    /**
     * quantity
     */
    private Integer quantity;

    /**
     * owner
     */
    private String owner;

    /**
     * petid
     */
    private Integer petid;

    /**
     * flag
     */
    private Integer flag;

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
