package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.NxcouponsDO;
import org.gms.dao.mapper.NxcouponsMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 点券优惠券业务服务，管理 NX 优惠券的创建与核销。
 */
@Service
@AllArgsConstructor
public class NxCouponService {
    private final NxcouponsMapper nxcouponsMapper;

    /**
     * 执行 selectActiveCouponIds 相关业务逻辑。
     *
     * @param weekDay weekDay
     * @param hourDay hourDay
     * @return List<Integer> 类型结果
     */
    public List<Integer> selectActiveCouponIds(int weekDay, int hourDay) {
        return nxcouponsMapper.selectActiveCouponIds(weekDay, hourDay);
    }

    /**
     * 执行 getNxCoupons 相关业务逻辑。
     *
     * @param condition condition
     * @return List<NxcouponsDO> 类型结果
     */
    public List<NxcouponsDO> getNxCoupons(NxcouponsDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return nxcouponsMapper.selectListByQuery(queryWrapper);
    }
}
