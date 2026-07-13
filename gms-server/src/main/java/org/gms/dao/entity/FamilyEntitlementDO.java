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
 * 家族权限实体类，对应数据库表 family_entitlement。
 * 存储家族成员权限配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("family_entitlement")
public class FamilyEntitlementDO implements Serializable {

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
    private Integer charid;

    /**
     * entitlementid
     */
    private Integer entitlementid;

    /**
     * 时间戳
     */
    private Long timestamp;

}
