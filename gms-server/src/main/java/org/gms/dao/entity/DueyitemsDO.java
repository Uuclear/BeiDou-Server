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
 * 快递物品实体类，对应数据库表 dueyitems。
 * 存储快递包裹物品明细。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dueyitems")
public class DueyitemsDO implements Serializable {

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
     * 包裹ID
     */
    private Long packageid;

    /**
     * 物品栏物品ID
     */
    private Long inventoryitemid;

}
