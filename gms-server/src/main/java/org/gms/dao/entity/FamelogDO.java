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
 * 人气记录实体类，对应数据库表 famelog。
 * 存储人气变更日志。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("famelog")
public class FamelogDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * famelogid
     */
    private Integer famelogid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * characteridTo
     */
    private Integer characteridTo;

    /**
     * when
     */
    private Timestamp when;

}
