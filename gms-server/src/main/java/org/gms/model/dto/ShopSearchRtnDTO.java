package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NPC 商店搜索响应 DTO，返回商店基本信息及关联 NPC 名称等展示字段。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopSearchRtnDTO {
    private Long shopId;
    private Integer npcId;
    private String npcName;
}
