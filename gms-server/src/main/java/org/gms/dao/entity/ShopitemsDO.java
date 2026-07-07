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
 * 数据库表 `shopitems` 的实体类（DO）。
 * <p>
 * NPC 商店售卖物品表，定义各商店中道具的价格、库存与刷新规则。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("shopitems")
public class ShopitemsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long shopitemid;

    private Long shopid;

    private Integer itemid;

    private Integer price;

    private Integer pitch;

    /**
     * sort is an arbitrary field designed to give leeway when modifying shops. The lowest number is 104 and it increments by 4 for each item to allow decent space for swapping/inserting/removing items.
     */
    private Integer position;

}
