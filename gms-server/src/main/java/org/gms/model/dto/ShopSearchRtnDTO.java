package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商店搜索结果返回DTO
 * 用于返回商店的基本信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopSearchRtnDTO {
    /**
     * 商店ID
     */
    private Long shopId;

    /**
     * NPC ID
     */
    private Integer npcId;

    /**
     * NPC名称
     */
    private String npcName;
}
