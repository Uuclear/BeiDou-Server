package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.SkillsDO;

/**
 * `skills` 表 / {@link org.gms.dao.entity.SkillsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 角色已学技能表，记录技能 ID、当前等级、主技能等级及过期时间。
 */
public interface SkillsMapper extends BaseMapper<SkillsDO> {

}
