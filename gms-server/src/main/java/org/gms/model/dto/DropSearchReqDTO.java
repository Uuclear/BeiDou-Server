package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 掉落物搜索请求DTO
 * 用于查询怪物掉落信息的请求参数，支持分页和多条件筛选
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DropSearchReqDTO extends BasePageDTO {
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
     * 任务ID
     */
    private Integer questId;
}
