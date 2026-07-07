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
 * 数据库表 `drop_data` 的实体类（DO）。
 * <p>
 * 怪物掉落配置表，定义各怪物死亡后掉落物品的概率与数量。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("drop_data")
public class DropDataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer dropperid;

    private Integer itemid;

    private Integer minimumQuantity;

    private Integer maximumQuantity;

    private Integer questid;

    private Integer chance;

}
