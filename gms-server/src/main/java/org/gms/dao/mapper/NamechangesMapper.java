package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.NamechangesDO;

/**
 * `namechanges` 表 / {@link org.gms.dao.entity.NamechangesDO} 的 MyBatis Mapper 接口。
 * <p>
 * 角色改名记录表，跟踪角色名称变更历史与审核状态。
 */
public interface NamechangesMapper extends BaseMapper<NamechangesDO> {

}
