package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 游戏配置类型枚举响应 DTO，返回配置项的主类型 types 与子类型 subTypes 列表。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigTypeDTO {
    private List<String> types;
    private List<String> subTypes;
}
