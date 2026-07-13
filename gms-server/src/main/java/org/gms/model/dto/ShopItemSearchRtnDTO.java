package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商店物品搜索结果返回DTO
 * 用于返回商店物品的详细信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemSearchRtnDTO {
    /**
     * 商店物品ID
     */
    private Long id;

    /**
     * 商店ID
     */
    private Long shopId;

    /**
     * 物品ID
     */
    private Integer itemId;

    /**
     * 物品价格
     */
    private Integer price;

    /**
     * 折扣价格
     */
    private Integer pitch;

    /**
     * 物品在商店中的位置
     */
    private Integer position;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 物品描述
     */
    private String itemDesc;
}
