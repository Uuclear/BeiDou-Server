package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线角色列表查询请求DTO
 * 用于查询在线玩家列表的请求参数，支持分页和条件筛选
 */
@Getter
@Setter
public class ChrOnlineListReqDTO extends BasePageDTO {
    /**
     * 角色ID
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 地图ID
     */
    private Integer map;

    /**
     * 世界ID
     */
    private int world;
}
