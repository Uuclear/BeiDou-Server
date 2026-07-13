package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.gms.dao.entity.ModifiedCashItemDO;

/**
 * 商城商品批量上下架请求DTO
 * 用于批量修改商城商品的上架状态
 */
@Getter
@Setter
public class CashShopBatchOnSaleReqDTO {
    /**
     * 要修改的商城商品数据数组
     */
    private ModifiedCashItemDO[] data;

    /**
     * 修改类型
     */
    private String type;

    /**
     * 修改值
     */
    private Integer value;
}
