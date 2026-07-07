package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.GuildsDO;

/**
 * `guilds` 表 / {@link org.gms.dao.entity.GuildsDO} 的 MyBatis Mapper 接口。
 * <p>
 * 公会信息表，存储公会名称、等级、会长、徽章及公告等数据。
 */
public interface GuildsMapper extends BaseMapper<GuildsDO> {

}
