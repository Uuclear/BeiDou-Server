package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gms.dao.entity.GachaponRewardPoolDO;

/**
 * 转蛋机奖池搜索结果返回DTO
 * 用于返回转蛋机奖池奖励信息，包含实际概率
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GachaponPoolSearchRtnDTO extends GachaponRewardPoolDO {
    /**
     * 实际概率值
     */
    private Integer realProb;
}
