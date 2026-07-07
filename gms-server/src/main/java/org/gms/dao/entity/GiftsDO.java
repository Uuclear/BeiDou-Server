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
 * 数据库表 `gifts` 的实体类（DO）。
 * <p>
 * 现金商城礼物表，记录玩家购买并赠送给他人的商城道具。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("gifts")
public class GiftsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer to;

    private String from;

    private String message;

    private Long sn;

    private Integer ringid;

}
