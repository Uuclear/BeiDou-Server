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
 * 数据库表 `worldtransfers` 的实体类（DO）。
 * <p>
 * 跨世界/频道角色转移记录，跟踪角色在不同世界间的迁移申请与状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("worldtransfers")
public class WorldtransfersDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Integer from;

    private Integer to;

    @Column("requestTime")
    private Timestamp requestTime;

    @Column("completionTime")
    private Timestamp completionTime;

}
