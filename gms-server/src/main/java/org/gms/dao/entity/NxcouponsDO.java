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
 * NX优惠券实体类，对应数据库表 nxcoupons。
 * 存储NX优惠券数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("nxcoupons")
public class NxcouponsDO implements Serializable {

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
     * couponid
     */
    private Integer couponid;

    /**
     * rate
     */
    private Integer rate;

    /**
     * activeday
     */
    private Integer activeday;

    /**
     * starthour
     */
    private Integer starthour;

    /**
     * endhour
     */
    private Integer endhour;

}
