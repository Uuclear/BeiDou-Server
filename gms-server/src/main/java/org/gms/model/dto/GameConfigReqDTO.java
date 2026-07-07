package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 游戏配置查询/修改请求 DTO，按配置类型与键名分页检索 game_config 表条目。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GameConfigReqDTO extends BasePageDTO {
    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数子类型
     */
    private String subType;

    /**
     * 搜索文本：名称、描述
     */
    private String filter;
}
