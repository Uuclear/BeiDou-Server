package org.gms.model.dto;

import lombok.*;

import java.util.List;

/**
 * 在线角色列表响应 DTO，返回角色 ID、名称、等级、地图及频道等在线信息。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChrOnlineListRtnDTO {
    private int world;
    private int id;
    private String name;
    private int map;
    private int job;
    private String jobName;
    private int level;
    private int gm;

}
