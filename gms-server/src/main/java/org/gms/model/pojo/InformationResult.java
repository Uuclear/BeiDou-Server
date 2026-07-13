package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 信息查询结果实体类
 * 用于封装通用信息查询的返回结果，包括类型、ID、名称和描述
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResult {
    /**
     * 信息类型
     */
    private String type;

    /**
     * 信息ID
     */
    private Integer id;

    /**
     * 信息名称
     */
    private String name;

    /**
     * 信息描述
     */
    private String desc;
}
