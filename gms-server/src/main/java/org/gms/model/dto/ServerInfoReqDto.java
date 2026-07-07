package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务器信息查询请求 DTO，指定要获取的世界或频道等服务端运行参数。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerInfoReqDto {
    private List<Integer> worldIdList;

}
