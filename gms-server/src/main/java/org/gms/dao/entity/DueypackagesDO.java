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
 * 快递包裹实体类，对应数据库表 dueypackages。
 * 存储快递包裹信息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dueypackages")
public class DueypackagesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 包裹ID
     */
    private Long packageid;

    /**
     * 收件人角色ID
     */
    private Long receiverid;

    /**
     * 寄件人名称
     */
    private String sendername;

    /**
     * mesos
     */
    private Long mesos;

    /**
     * 时间戳
     */
    private Timestamp timestamp;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 已查看/已领取状态
     */
    private Integer checked;

    /**
     * 类型
     */
    private Integer type;

}
