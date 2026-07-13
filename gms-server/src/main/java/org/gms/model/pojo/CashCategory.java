package org.gms.model.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.model.dto.BasePageDTO;

/**
 * 商城分类信息实体类
 * 用于表示商城中的商品分类及其子分类信息，支持分页查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CashCategory extends BasePageDTO {
    /**
     * 分类ID
     */
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 子分类ID
     */
    private Integer subId;

    /**
     * 子分类名称
     */
    private String subName;

    /**
     * 是否在售
     */
    private Boolean onSale;

    /**
     * 商品ID
     */
    private Integer itemId;
}
