package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务器信息请求DTO
 * 用于查询服务器信息的请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerInfoReqDto {
    /**
     * 世界ID列表
     */
    private List<Integer> worldIdList;

}
