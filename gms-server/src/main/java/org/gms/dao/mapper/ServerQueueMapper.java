package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.ServerQueueDO;

/**
 * `server_queue` 表 / {@link org.gms.dao.entity.ServerQueueDO} 的 MyBatis Mapper 接口。
 * <p>
 * 服务器登录排队队列表，管理高峰时段玩家登录顺序与队列状态。
 */
public interface ServerQueueMapper extends BaseMapper<ServerQueueDO> {

}
