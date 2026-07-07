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
 * 数据库表 `extend_value` 的实体类（DO）。
 * <p>
 * 扩展键值存储表，以类型+键的方式保存各类业务的附加字段。
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
     * 扩展字段id
     */
    @Id
    private String extendId;

    /**
     * 扩展字段类型，11-账号，12-账号日清，13-账号周清；21-角色，22-角色日清，23-角色周清
     */
    @Id
    private String extendType;

    /**
     * 扩展字段名称
     */
    @Id
    private String extendName;

    /**
     * 扩展字段值
     */
    private String extendValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
