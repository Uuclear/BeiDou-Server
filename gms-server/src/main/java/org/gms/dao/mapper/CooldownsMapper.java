package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.CooldownsDO;

/**
 * `cooldowns` 表 / {@link org.gms.dao.entity.CooldownsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 冷却时间表，记录技能、物品或活动的剩余冷却毫秒数。
 */
public interface CooldownsMapper extends BaseMapper<CooldownsDO> {

}
