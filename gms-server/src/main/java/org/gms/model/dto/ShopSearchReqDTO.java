package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NPC 商店搜索请求 DTO，支持按商店 ID、NPC、道具等条件分页查询 shops/shopitems。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopSearchReqDTO extends BasePageDTO {
    private Long shopId;
    private Integer npcId;
    private String npcName;
    private Integer itemId;
    private String itemName;
}
