package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

/**
 * 扩展值实体类，对应数据库表 extend_value。
 * 存储通用扩展键值对数据，支持按账号/角色维度存储扩展信息，包括日级和周级清类型。
 *
 * @author Feras
 * @since 2025-10-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("extend_value")
public class ExtendValueDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 扩展字段所属ID（账号ID或角色ID）
     */
    @Id
    private String extendId;

    /**
     * 扩展字段类型：11-账号级，12-账号日级，13-账号周级；21-角色级，22-角色日级，23-角色周级
     */
    @Id
    private String extendType;

    /**
     * 扩展字段名称（键名）
     */
    @Id
    private String extendName;

    /**
     * 扩展字段值
     */
    private String extendValue;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 记录更新时间
     */
    private Date updateTime;

}
