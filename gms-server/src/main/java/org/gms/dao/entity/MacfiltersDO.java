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
 * 数据库表 `macfilters` 的实体类（DO）。
 * <p>
 * MAC 地址过滤/白名单表，用于限制或放行特定设备的登录。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("macfilters")
public class MacfiltersDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long macfilterid;

    private String filter;

}
