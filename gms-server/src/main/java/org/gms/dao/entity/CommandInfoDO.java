package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * 命令信息实体类，对应数据库表 command_info。
 * 存储GM命令配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Table("command_info")
public class CommandInfoDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 命令语法说明
     */
    private String syntax;

    /**
     * 默认权限等级
     */
    private Integer defaultLevel;

    /**
     * 命令处理类名
     */
    private String clazz;

    /**
     * 是否启用
     */
    private boolean enabled;

}
