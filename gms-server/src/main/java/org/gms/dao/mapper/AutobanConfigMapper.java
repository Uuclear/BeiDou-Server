package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.AutobanConfigDO;

/**
 * `autoban_config` 表 / {@link org.gms.dao.entity.AutobanConfigDO} 的 MyBatis Mapper 接口。
 * <p>
 * 自动封禁规则配置表，定义各类反作弊检测的积分阈值与周期。
 */
public interface AutobanConfigMapper extends BaseMapper<AutobanConfigDO> {
}
