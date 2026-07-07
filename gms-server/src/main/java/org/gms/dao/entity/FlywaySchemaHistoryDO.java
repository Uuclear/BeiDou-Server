package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `flyway_schema_history` 的实体类（DO）。
 * <p>
 * Flyway 数据库迁移历史表，由 Flyway 自动维护的版本执行记录。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("flyway_schema_history")
public class FlywaySchemaHistoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer installedRank;

    private String version;

    private String description;

    private String type;

    private String script;

    private Integer checksum;

    private String installedBy;

    private Timestamp installedOn;

    private Integer executionTime;

    private Boolean success;

}
