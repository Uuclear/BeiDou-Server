package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 背包/角色物品搜索请求 DTO，支持按背包类型、角色 ID/名称及在线状态分页查询。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventorySearchReqDTO extends BasePageDTO {
    private Byte inventoryType;
    private Integer characterId;
    private String characterName;
    private boolean onlineStatus;
}
