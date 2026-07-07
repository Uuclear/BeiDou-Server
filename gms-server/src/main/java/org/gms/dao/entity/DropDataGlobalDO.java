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
 * 数据库表 `drop_data_global` 的实体类（DO）。
 * <p>
 * 全局掉落配置表，定义不绑定特定怪物的通用掉落规则。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("drop_data_global")
public class DropDataGlobalDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer continent;

    private Integer itemid;

    private Integer minimumQuantity;

    private Integer maximumQuantity;

    private Integer questid;

    private Integer chance;

    private String comments;

}
