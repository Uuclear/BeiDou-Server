package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `makerrewarddata` 的实体类（DO）。
 * <p>
 * 制作人系统产出奖励表，定义制作成功后的额外奖励物品。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makerrewarddata")
public class MakerrewarddataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer itemid;

    @Id
    private Integer rewardid;

    private Integer quantity;

    private Integer prob;

}
