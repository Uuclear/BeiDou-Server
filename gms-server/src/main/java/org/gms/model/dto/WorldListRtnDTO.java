package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 游戏世界列表响应 DTO，返回各世界的经验、掉落、金币等倍率配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldListRtnDTO {
    private Integer id;
    private Float expRate;
    private Float dropRate;
    private Float mesoRate;
    private Float bossDropRate;
    private Float questRate;
    private Float travelRate;
    private Float fishingRate;
}
