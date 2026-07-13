package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求提交体封装类
 * 用于统一封装所有API请求，包含请求ID和请求数据
 * @param <T> 请求数据的类型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    /**
     * 请求唯一标识ID，用于请求追踪
     */
    private String requestId;

    /**
     * 请求数据
     */
    private T data;
}
