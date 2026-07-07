package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.gms.dao.entity.FlywaySchemaHistoryDO;

/**
 * `flyway_schema_history` 表 / {@link org.gms.dao.entity.FlywaySchemaHistoryDO} 的 MyBatis Mapper 接口。
 * <p>
 * Flyway 数据库迁移历史表，由 Flyway 自动维护的版本执行记录。
 */
public interface FlywaySchemaHistoryMapper extends BaseMapper<FlywaySchemaHistoryDO> {

}
