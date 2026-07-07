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
 * 数据库表 `wishlists` 的实体类（DO）。
 * <p>
 * 现金商城心愿单，记录玩家关注的待购道具条目。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("wishlists")
public class WishlistsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer charid;

    private Integer sn;

}
