package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.FlywaySchemaHistoryDO;

/**
 * 数据库版本历史数据访问Mapper接口，对应数据库表 flyway_schema_history。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface FlywaySchemaHistoryMapper extends BaseMapper<FlywaySchemaHistoryDO> {

}
