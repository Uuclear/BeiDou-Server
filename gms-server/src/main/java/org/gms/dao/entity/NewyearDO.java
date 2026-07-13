package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 新年活动实体类，对应数据库表 newyear。
 * 存储新年活动数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("newyear")
public class NewyearDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Long id;

    /**
     * senderid
     */
    private Integer senderid;

    /**
     * 寄件人名称
     */
    private String sendername;

    /**
     * 收件人角色ID
     */
    private Integer receiverid;

    /**
     * receivername
     */
    private String receivername;

    /**
     * 消息内容
     */
    private String message;

    /**
     * senderdiscard
     */
    private Boolean senderdiscard;

    /**
     * receiverdiscard
     */
    private Boolean receiverdiscard;

    /**
     * received
     */
    private Boolean received;

    /**
     * timesent
     */
    private Long timesent;

    /**
     * timereceived
     */
    private Long timereceived;

}
