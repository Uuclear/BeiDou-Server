package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 频道列表响应 DTO，描述各游戏频道的 ID、名称、在线人数及状态。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelListRtnDTO {
    private Integer id;
    private Integer worldId;
}
