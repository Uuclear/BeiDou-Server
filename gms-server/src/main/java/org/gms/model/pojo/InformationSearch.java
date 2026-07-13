package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 信息搜索条件实体类
 * 用于封装信息查询时的搜索条件，支持按类型和过滤条件进行搜索
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationSearch {
    /**
     * 接口参数，Web端传参
     * types - 信息类型列表，对应InformationType枚举
     * filter - 过滤条件，可以是ID或名称
     */
    private List<String> types;

    /**
     * 过滤条件字符串
     */
    private String filter;

    /**
     * 非接口参数，服务内部调用使用
     * filterType - 过滤类型：0-同时匹配ID和名称，1-仅匹配ID，2-仅匹配名称
     * fullMatch - 是否精确匹配
     */
    private int filterType;

    /**
     * 是否精确匹配
     */
    private boolean fullMatch;
}
