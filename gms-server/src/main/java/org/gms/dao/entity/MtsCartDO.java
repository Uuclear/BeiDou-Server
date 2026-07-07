package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `mts_cart` 的实体类（DO）。
 * <p>
 * 拍卖行购物车表，记录玩家待结算的 MTS 购买条目。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("mts_cart")
public class MtsCartDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer cid;

    private Integer itemid;

}
