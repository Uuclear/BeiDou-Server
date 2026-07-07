package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 分页查询基类 DTO，封装通用分页参数 pageNo、pageSize、onlyTotal、notPage；出参分页结果统一使用 MyBatis-Flex 的 Page。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BasePageDTO {
    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 只统计总数
     */
    private boolean onlyTotal;
    /**
     * 不分页
     */
    private boolean notPage;
}
