package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.dao.entity.InventoryequipmentDO;

/**
 * 装备栏查询请求DTO
 * 用于查询装备详细信息的请求参数，支持分页
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventoryequipmentReqDTO extends BasePageDTO {

    /**
     * 装备记录ID
     */
    private Long inventoryequipmentid;

    /**
     * 背包物品ID（外键）
     */
    private Long inventoryitemid;

}
