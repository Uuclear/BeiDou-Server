package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.IpbansDO;

/**
 * `ipbans` 表 / {@link org.gms.dao.entity.IpbansDO} 的 MyBatis Mapper 接口。
 * <p>
 * IP 封禁表，记录被封禁的 IP 地址及封禁原因与有效期。
 */
public interface IpbansMapper extends BaseMapper<IpbansDO> {

}
