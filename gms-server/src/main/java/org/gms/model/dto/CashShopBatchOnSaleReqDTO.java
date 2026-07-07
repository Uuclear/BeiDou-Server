package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.gms.dao.entity.ModifiedCashItemDO;

/**
 * 现金商城批量上下架请求 DTO，用于批量修改商城道具售卖状态。
 */
@Getter
@Setter
public class CashShopBatchOnSaleReqDTO {
    private ModifiedCashItemDO[] data;
    private String type;
    private Integer value;
}
