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
 * 传送石位置实体类，对应数据库表 trocklocations。
 * 存储VIP传送石位置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("trocklocations")
public class TrocklocationsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * trockid
     */
    private Integer trockid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * mapid
     */
    private Integer mapid;

    /**
     * vip
     */
    private Integer vip;

}
