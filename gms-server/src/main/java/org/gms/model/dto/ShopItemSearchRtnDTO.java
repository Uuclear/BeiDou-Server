package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商店道具详情响应 DTO，返回 shopitems 表条目及关联的 NPC/道具展示信息。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemSearchRtnDTO {
    private Long id;
    private Long shopId;
    private Integer itemId;
    private Integer price;
    private Integer pitch;
    private Integer position;
    private String itemName;
    private String itemDesc;
}
