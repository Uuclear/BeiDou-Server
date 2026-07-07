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
 * 数据库表 `dueyitems` 的实体类（DO）。
 * <p>
 * Duey 快递物品表，存储包裹内附带的道具明细。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dueyitems")
public class DueyitemsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long packageid;

    private Long inventoryitemid;

}
