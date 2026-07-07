package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.NxcouponsDO;

import java.util.List;

/**
 * `nxcoupons` 表 / {@link org.gms.dao.entity.NxcouponsDO} 的 MyBatis Mapper 接口。
 * <p>
 * NX 优惠券表，定义按星期与时段生效的商城折扣券规则。
 */
public interface NxcouponsMapper extends BaseMapper<NxcouponsDO> {
    /**
     * 查询当前星期与小时段内生效的 NX 优惠券 ID 列表。
     */
    @Select("SELECT couponid FROM nxcoupons WHERE (activeday & #{weekDay}) = #{weekDay} AND starthour <= #{hourDay} AND endhour > #{hourDay}")
    List<Integer> selectActiveCouponIds(@Param("weekDay") int weekDay, @Param("hourDay") int hourDay);
}
