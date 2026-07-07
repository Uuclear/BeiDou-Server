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
 * 数据库表 `inventorymerchant` 的实体类（DO）。
 * <p>
 * 自由市场/雇佣商人摊位表，存储玩家开设的离线商店物品与 meso。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventorymerchant")
public class InventorymerchantDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long inventorymerchantid;

    private Long inventoryitemid;

    private Integer characterid;

    private Integer bundles;

}
