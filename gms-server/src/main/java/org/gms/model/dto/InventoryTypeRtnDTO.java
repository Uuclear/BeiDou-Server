package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 背包类型枚举响应 DTO，返回装备/消耗/设置/其他/现金各背包栏的类型编码与名称。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryTypeRtnDTO {
    private Byte inventoryType;
    private String name;
}
