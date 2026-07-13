package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
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
 * 改名记录实体类，对应数据库表 namechanges。
 * 存储角色改名历史。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("namechanges")
public class NamechangesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * 角色ID
     */
    private Integer characterid;

    @Column("old")
    /**
     * older
     */
    private String older;

    @Column("new")
    /**
     * newer
     */
    private String newer;

    @Column("requestTime")
    /**
     * requestTime
     */
    private Timestamp requestTime;

    @Column("completionTime")
    /**
     * completionTime
     */
    private Timestamp completionTime;

}
