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
 * 数据库表 `trocklocations` 的实体类（DO）。
 * <p>
 * 神秘稳居与高级神秘稳居传送点记录，保存角色注册的自定义传送坐标。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("trocklocations")
public class TrocklocationsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer trockid;

    private Integer characterid;

    private Integer mapid;

    private Integer vip;

}
