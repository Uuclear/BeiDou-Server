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
 * 数据库表 `family_entitlement` 的实体类（DO）。
 * <p>
 * 家族权益表，定义家族成员可享有的经验加成等特权配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("family_entitlement")
public class FamilyEntitlementDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer charid;

    private Integer entitlementid;

    private Long timestamp;

}
