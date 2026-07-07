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
 * 数据库表 `rings` 的实体类（DO）。
 * <p>
 * 戒指关系表，存储情侣戒指、好友戒指等双人绑定道具的配对信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("rings")
public class RingsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("partnerRingId")
    private Integer partnerRingId;

    @Column("partnerChrId")
    private Integer partnerChrId;

    private Integer itemid;

    private String partnername;

}
