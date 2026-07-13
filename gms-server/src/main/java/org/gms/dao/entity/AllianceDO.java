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
 * 家族联盟实体类，对应数据库表 alliance。
 * 存储多个家族组成的联盟信息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("alliance")
public class AllianceDO implements Serializable {

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
     * 名称
     */
    private String name;

    /**
     * 容量上限
     */
    private Long capacity;

    /**
     * 公告内容
     */
    private String notice;

    /**
     * rank1
     */
    private String rank1;

    /**
     * rank2
     */
    private String rank2;

    /**
     * rank3
     */
    private String rank3;

    /**
     * rank4
     */
    private String rank4;

    /**
     * rank5
     */
    private String rank5;

}
