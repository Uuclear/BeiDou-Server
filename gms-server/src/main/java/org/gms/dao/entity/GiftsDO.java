package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 礼物实体类，对应数据库表 gifts。
 * 存储角色间赠送礼物记录。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("gifts")
public class GiftsDO implements Serializable {

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
     * to
     */
    private Integer to;

    /**
     * from
     */
    private String from;

    /**
     * 消息内容
     */
    private String message;

    /**
     * sn
     */
    private Long sn;

    /**
     * ringid
     */
    private Integer ringid;

}
