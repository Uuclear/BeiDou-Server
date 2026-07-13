package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 配置类型DTO
 * 用于返回游戏配置的类型和子类型列表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigTypeDTO {
    /**
     * 配置类型列表
     */
    private List<String> types;

    /**
     * 配置子类型列表
     */
    private List<String> subTypes;
}
