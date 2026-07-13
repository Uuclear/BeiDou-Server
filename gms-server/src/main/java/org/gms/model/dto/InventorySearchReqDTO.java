package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 背包物品搜索请求DTO
 * 用于查询玩家背包物品的请求参数，支持分页和条件筛选
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventorySearchReqDTO extends BasePageDTO {
    /**
     * 背包类型
     */
    private Byte inventoryType;

    /**
     * 角色ID
     */
    private Integer characterId;

    /**
     * 角色名称
     */
    private String characterName;

    /**
     * 是否只查询在线玩家
     */
    private boolean onlineStatus;
}
