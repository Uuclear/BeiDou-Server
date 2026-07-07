package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.MacfiltersDO;

/**
 * `macfilters` 表 / {@link org.gms.dao.entity.MacfiltersDO} 的 MyBatis Mapper 接口。
 * <p>
 * MAC 地址过滤/白名单表，用于限制或放行特定设备的登录。
 */
public interface MacfiltersMapper extends BaseMapper<MacfiltersDO> {

}
