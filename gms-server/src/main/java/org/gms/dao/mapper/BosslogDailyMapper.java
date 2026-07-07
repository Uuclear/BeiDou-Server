package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.BosslogDailyDO;

/**
 * `bosslog_daily` 表 / {@link org.gms.dao.entity.BosslogDailyDO} 的 MyBatis Mapper 接口。
 * <p>
 * 日 Boss 击杀记录表，限制角色每日对特定 Boss 的挑战次数。
 */
public interface BosslogDailyMapper extends BaseMapper<BosslogDailyDO> {

}
