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
 * 数据库表 `specialcashitems` 的实体类（DO）。
 * <p>
 * 特殊现金商城道具配置，定义限时或活动类商城售卖条目。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("specialcashitems")
public class SpecialcashitemsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private Integer sn;

    /**
     * 1024 is add/remove
     */
    private Integer modifier;

    private Integer info;

}
