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
 * 数据库表 `server_queue` 的实体类（DO）。
 * <p>
 * 服务器登录排队队列表，管理高峰时段玩家登录顺序与队列状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("server_queue")
public class ServerQueueDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer accountid;

    private Integer characterid;

    private Integer type;

    private Integer value;

    private String message;

    @Column("createTime")
    private Timestamp createTime;

}
