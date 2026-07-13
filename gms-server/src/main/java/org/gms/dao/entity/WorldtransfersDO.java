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
 * 世界转移实体类，对应数据库表 worldtransfers。
 * 存储跨服务器转移记录。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("worldtransfers")
public class WorldtransfersDO implements Serializable {

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

    /**
     * from
     */
    private Integer from;

    /**
     * to
     */
    private Integer to;

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
