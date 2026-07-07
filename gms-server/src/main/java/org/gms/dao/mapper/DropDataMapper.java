package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.DropDataDO;

/**
 * `drop_data` 表 / {@link org.gms.dao.entity.DropDataDO} 的 MyBatis Mapper 接口。
 * <p>
 * 怪物掉落配置表，定义各怪物死亡后掉落物品的概率与数量。
 */
public interface DropDataMapper extends BaseMapper<DropDataDO> {

}
