package org.gms.model.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.model.dto.BasePageDTO;

/**
 * 现金商城分类查询 POJO，继承分页参数并携带主/子分类 ID、名称及上下架筛选条件，供商城检索服务内部使用。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CashCategory extends BasePageDTO {
    private Integer id;
    private String name;
    private Integer subId;
    private String subName;
    private Boolean onSale;
    private Integer itemId;
}
