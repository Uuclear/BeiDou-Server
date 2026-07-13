package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 世界列表返回DTO
 * 用于返回游戏世界（服务器）的配置信息，包括各种倍率设置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldListRtnDTO {
    /**
     * 世界ID
     */
    private Integer id;

    /**
     * 经验倍率
     */
    private Float expRate;

    /**
     * 掉落倍率
     */
    private Float dropRate;

    /**
     * 金币倍率
     */
    private Float mesoRate;

    /**
     * Boss掉落倍率
     */
    private Float bossDropRate;

    /**
     * 任务经验倍率
     */
    private Float questRate;

    /**
     * 旅行速度倍率
     */
    private Float travelRate;

    /**
     * 钓鱼倍率
     */
    private Float fishingRate;
}
