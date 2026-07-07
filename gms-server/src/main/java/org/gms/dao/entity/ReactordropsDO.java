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
 * 数据库表 `reactordrops` 的实体类（DO）。
 * <p>
 * 地图反应堆掉落配置表，定义反应堆被破坏后产出的物品与概率。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reactordrops")
public class ReactordropsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long reactordropid;

    private Integer reactorid;

    private Integer itemid;

    private Integer chance;

    private Integer questid;

}
