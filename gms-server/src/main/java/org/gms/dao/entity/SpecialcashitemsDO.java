package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 特殊现金物品实体类，对应数据库表 specialcashitems。
 * 存储特殊现金物品配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("specialcashitems")
public class SpecialcashitemsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * sn
     */
    private Integer sn;

    /**
     * 1024 is add/remove
     */
    private Integer modifier;

    /**
     * 信息内容
     */
    private Integer info;

}
