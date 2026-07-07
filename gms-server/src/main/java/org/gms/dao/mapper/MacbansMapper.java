package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.MacbansDO;

/**
 * `macbans` 表 / {@link org.gms.dao.entity.MacbansDO} 的 MyBatis Mapper 接口。
 * <p>
 * MAC 地址封禁表，记录因违规被封禁的硬件 MAC 及封禁期限。
 */
public interface MacbansMapper extends BaseMapper<MacbansDO> {

}
