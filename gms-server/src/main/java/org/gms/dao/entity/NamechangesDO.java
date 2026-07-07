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
 * 数据库表 `namechanges` 的实体类（DO）。
 * <p>
 * 角色改名记录表，跟踪角色名称变更历史与审核状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("namechanges")
public class NamechangesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    @Column("old")
    private String older;

    @Column("new")
    private String newer;

    @Column("requestTime")
    private Timestamp requestTime;

    @Column("completionTime")
    private Timestamp completionTime;

}
