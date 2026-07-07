package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 扭蛋奖池搜索请求 DTO，支持按奖池名称、类型等条件分页查询 gachapon_reward_pool。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GachaponPoolSearchReqDTO extends BasePageDTO {
    private Integer gachaponId;
}
