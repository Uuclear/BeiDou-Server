package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息检索结果 POJO，封装检索到的资源类型 type、ID、名称 name 与描述 desc，用于通用下拉/联想查询。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResult {
    private String type;
    private Integer id;
    private String name;
    private String desc;
}
