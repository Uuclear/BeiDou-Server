package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.GameConfigDO;

/**
 * `game_config` 表 / {@link org.gms.dao.entity.GameConfigDO} 的 MyBatis Mapper 接口。
 * <p>
 * 游戏运行时配置表，以键值对形式存储可热更新的服务端参数。
 */
public interface GameConfigMapper extends BaseMapper<GameConfigDO> {

}
