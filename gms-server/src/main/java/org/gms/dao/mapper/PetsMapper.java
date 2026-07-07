package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.PetsDO;

/**
 * `pets` 表 / {@link org.gms.dao.entity.PetsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 宠物数据表，持久化宠物等级、亲密度、技能、装备及饱食度等信息。
 */
public interface PetsMapper extends BaseMapper<PetsDO> {

}
