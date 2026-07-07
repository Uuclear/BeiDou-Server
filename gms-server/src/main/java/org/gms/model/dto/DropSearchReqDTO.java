package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 怪物掉落配置搜索请求 DTO，支持按掉落者、大陆、道具、任务等条件分页查询 drop_data。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DropSearchReqDTO extends BasePageDTO {
    private Integer dropperId;
    private String dropperName;
    private Integer continent;
    private Integer itemId;
    private String itemName;
    private Integer questId;
}
