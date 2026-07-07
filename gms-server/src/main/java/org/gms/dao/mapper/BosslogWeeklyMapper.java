package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.BosslogWeeklyDO;

/**
 * `bosslog_weekly` 表 / {@link org.gms.dao.entity.BosslogWeeklyDO} 的 MyBatis Mapper 接口。
 * <p>
 * 周 Boss 击杀记录表，限制角色每周对特定 Boss 的挑战次数。
 */
public interface BosslogWeeklyMapper extends BaseMapper<BosslogWeeklyDO> {

}
