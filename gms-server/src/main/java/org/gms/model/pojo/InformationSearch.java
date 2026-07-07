package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 信息检索条件 POJO，供管理端与内部服务按类型列表 types 与关键字 filter 过滤游戏资源（怪物、道具、NPC 等）。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationSearch {
    /**
     * 接口参数，web传参
     * types = InformationType
     * filter = id or name
     */
    private List<String> types;
    private String filter;
    /**
     * 非接口参数，给服务内部调用
     * filterType = 0-both 1-id 2-name
     * fullMatch = 是否精确匹配
     */
    private int filterType;
    private boolean fullMatch;
}
