package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端 API 统一请求包装类，包含请求追踪 ID requestId 与业务载荷 data。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    private String requestId;
    private T data;
}
