package org.gms.model.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * GM命令查询/修改请求DTO
 * 用于管理游戏GM命令的请求参数，支持分页查询和条件筛选
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandReqDTO extends BasePageDTO {

    /**
     * 命令ID
     */
    private Integer id;

    /**
     * 命令等级（增改操作使用）
     */
    private Integer level;

    /**
     * 命令等级列表（多选条件查询使用）
     */
    private List<Integer> levelList;

    /**
     * 命令语法（游戏中实际输入的指令）
     */
    private String syntax;

    /**
     * 默认命令等级（增改操作使用）
     */
    private Integer defaultLevel;

    /**
     * 默认命令等级列表（查询使用）
     */
    private List<Integer> defaultLevelList;

    /**
     * 命令处理类
     */
    private String clazz;

    /**
     * 命令描述（支持模糊查询）
     */
    private String description;

    /**
     * 是否启用（精确查询）
     */
    private Boolean enabled;

}
