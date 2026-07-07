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
 * 数据库表 `nxcoupons` 的实体类（DO）。
 * <p>
 * NX 优惠券表，定义按星期与时段生效的商城折扣券规则。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("nxcoupons")
public class NxcouponsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer couponid;

    private Integer rate;

    private Integer activeday;

    private Integer starthour;

    private Integer endhour;

}
