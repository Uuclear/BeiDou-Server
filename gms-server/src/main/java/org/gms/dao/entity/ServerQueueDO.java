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
 * 服务器队列实体类，对应数据库表 server_queue。
 * 存储服务器排队数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("server_queue")
public class ServerQueueDO implements Serializable {

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
     * 账号ID
     */
    private Integer accountid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * 类型
     */
    private Integer type;

    /**
     * value
     */
    private Integer value;

    /**
     * 消息内容
     */
    private String message;

    @Column("createTime")
    /**
     * 创建时间
     */
    private Timestamp createTime;

}
