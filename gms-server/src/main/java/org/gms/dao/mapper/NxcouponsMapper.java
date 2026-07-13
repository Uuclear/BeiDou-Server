package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gms.dao.entity.NxcouponsDO;

import java.util.List;

/**
 * NX优惠券数据访问Mapper接口，对应数据库表 nxcoupons。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface NxcouponsMapper extends BaseMapper<NxcouponsDO> {
    /**
     * 查询NX优惠券数据
     */
    @Select("SELECT couponid FROM nxcoupons WHERE (activeday & #{weekDay}) = #{weekDay} AND starthour <= #{hourDay} AND endhour > #{hourDay}")
    List<Integer> selectActiveCouponIds(@Param("weekDay") int weekDay, @Param("hourDay") int hourDay);
}
