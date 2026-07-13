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
 * 怪物掉落实体类，对应数据库表 drop_data。
 * 存储怪物掉落物品配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("drop_data")
public class DropDataDO implements Serializable {

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
     * 掉落者ID（怪物ID）
     */
    private Integer dropperid;

    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * 最小数量
     */
    private Integer minimumQuantity;

    /**
     * 最大数量
     */
    private Integer maximumQuantity;

    /**
     * 关联任务ID
     */
    private Integer questid;

    /**
     * 掉落几率
     */
    private Integer chance;

}
