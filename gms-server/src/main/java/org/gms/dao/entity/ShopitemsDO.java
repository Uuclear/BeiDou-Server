package org.gms.dao.entity;

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
 * 商店物品实体类，对应数据库表 shopitems。
 * 存储商店出售物品配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("shopitems")
public class ShopitemsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * shopitemid
     */
    private Long shopitemid;

    /**
     * shopid
     */
    private Long shopid;

    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * price
     */
    private Integer price;

    /**
     * pitch
     */
    private Integer pitch;

    /**
     * sort is an arbitrary field designed to give leeway when modifying shops. The lowest number is 104 and it increments by 4 for each item to allow decent space for swapping/inserting/removing items.
     */
    private Integer position;

}
