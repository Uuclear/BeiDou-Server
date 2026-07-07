package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `famelog` 的实体类（DO）。
 * <p>
 * 人气变更日志表，追踪角色人气值的增减历史记录。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("famelog")
public class FamelogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer famelogid;

    private Integer characterid;

    private Integer characteridTo;

    private Timestamp when;

}
