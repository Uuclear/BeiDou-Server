package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线角色列表查询请求 DTO，支持按角色名、账号等条件分页检索在线玩家。
 */
@Getter
@Setter
public class ChrOnlineListReqDTO extends BasePageDTO {
    private Integer id;
    private String name;
    private Integer map;
    private int world;
}
