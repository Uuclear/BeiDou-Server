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
 * 数据库版本历史实体类，对应数据库表 flyway_schema_history。
 * Flyway迁移历史记录表。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("flyway_schema_history")
public class FlywaySchemaHistoryDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * installedRank
     */
    private Integer installedRank;

    /**
     * version
     */
    private String version;

    /**
     * 描述说明
     */
    private String description;

    /**
     * 类型
     */
    private String type;

    /**
     * script
     */
    private String script;

    /**
     * checksum
     */
    private Integer checksum;

    /**
     * installedBy
     */
    private String installedBy;

    /**
     * installedOn
     */
    private Timestamp installedOn;

    /**
     * executionTime
     */
    private Integer executionTime;

    /**
     * success
     */
    private Boolean success;

}
