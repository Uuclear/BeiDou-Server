package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 频道列表返回DTO
 * 用于返回游戏服务器频道信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelListRtnDTO {
    /**
     * 频道ID
     */
    private Integer id;

    /**
     * 世界ID
     */
    private Integer worldId;
}
