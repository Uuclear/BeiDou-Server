package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
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
 * 戒指实体类，对应数据库表 rings。
 * 存储角色佩戴的戒指数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("rings")
public class RingsDO implements Serializable {

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

    @Column("partnerRingId")
    /**
     * partnerRingId
     */
    private Integer partnerRingId;

    @Column("partnerChrId")
    /**
     * partnerChrId
     */
    private Integer partnerChrId;

    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * partnername
     */
    private String partnername;

}
