package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.dao.entity.InventoryequipmentDO;


/**
 * 背包装备分页查询请求 DTO，按角色与装备条件检索 inventoryequipment 记录。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventoryequipmentReqDTO  extends BasePageDTO{

    private Long inventoryequipmentid;

    private Long inventoryitemid;



}