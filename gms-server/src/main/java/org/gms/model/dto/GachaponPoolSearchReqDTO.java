package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 转蛋机奖池搜索请求DTO
 * 用于查询转蛋机奖池奖励的请求参数，支持分页
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GachaponPoolSearchReqDTO extends BasePageDTO {
    /**
     * 转蛋机ID
     */
    private Integer gachaponId;
}
