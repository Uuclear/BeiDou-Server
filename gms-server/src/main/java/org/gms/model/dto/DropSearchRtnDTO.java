package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 怪物掉落配置搜索响应 DTO，返回单条掉落规则的道具、概率及掉落者信息。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropSearchRtnDTO {
    private Long id;
    private Integer dropperId;
    private String dropperName;
    private Integer continent;
    private Integer itemId;
    private String itemName;
    private Integer minimumQuantity;
    private Integer maximumQuantity;
    private Integer questId;
    private String questName;
    private Integer chance;
    private String comments;
}
