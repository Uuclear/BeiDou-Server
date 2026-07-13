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
 * 反应器掉落实体类，对应数据库表 reactordrops。
 * 存储反应器掉落物品配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reactordrops")
public class ReactordropsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * reactordropid
     */
    private Long reactordropid;

    /**
     * reactorid
     */
    private Integer reactorid;

    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * 掉落几率
     */
    private Integer chance;

    /**
     * 关联任务ID
     */
    private Integer questid;

}
