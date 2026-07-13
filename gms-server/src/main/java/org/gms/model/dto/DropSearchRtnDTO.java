package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 掉落物搜索结果返回DTO
 * 用于返回怪物掉落的详细信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropSearchRtnDTO {
    /**
     * 掉落记录ID
     */
    private Long id;

    /**
     * 掉落者ID（怪物ID）
     */
    private Integer dropperId;

    /**
     * 掉落者名称（怪物名称）
     */
    private String dropperName;

    /**
     * 大陆ID
     */
    private Integer continent;

    /**
     * 物品ID
     */
    private Integer itemId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 最小掉落数量
     */
    private Integer minimumQuantity;

    /**
     * 最大掉落数量
     */
    private Integer maximumQuantity;

    /**
     * 任务ID
     */
    private Integer questId;

    /**
     * 任务名称
     */
    private String questName;

    /**
     * 掉落几率
     */
    private Integer chance;

    /**
     * 备注说明
     */
    private String comments;
}
