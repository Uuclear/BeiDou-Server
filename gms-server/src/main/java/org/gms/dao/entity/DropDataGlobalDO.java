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
 * 全局掉落实体类，对应数据库表 drop_data_global。
 * 存储区域全局掉落配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("drop_data_global")
public class DropDataGlobalDO implements Serializable {

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
     * 大陆ID
     */
    private Integer continent;

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

    /**
     * 备注说明
     */
    private String comments;

}
