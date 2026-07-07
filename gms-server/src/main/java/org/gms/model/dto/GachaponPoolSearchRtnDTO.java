package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gms.dao.entity.GachaponRewardPoolDO;

/**
 * 扭蛋奖池搜索响应 DTO，在奖池实体基础上扩展展示用字段，供管理端列表展示。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GachaponPoolSearchRtnDTO extends GachaponRewardPoolDO {
    private Integer realProb;
}
