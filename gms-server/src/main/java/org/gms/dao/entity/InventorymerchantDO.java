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
 * 商人仓库实体类，对应数据库表 inventorymerchant。
 * 存储个人商店物品库存。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventorymerchant")
public class InventorymerchantDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * inventorymerchantid
     */
    private Long inventorymerchantid;

    /**
     * 物品栏物品ID
     */
    private Long inventoryitemid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * bundles
     */
    private Integer bundles;

}
