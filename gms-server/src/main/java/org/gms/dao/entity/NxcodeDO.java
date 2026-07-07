package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `nxcode` 的实体类（DO）。
 * <p>
 * NX 兑换码主表，存储兑换码字符串、有效期及可兑换次数限制。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("nxcode")
public class NxcodeDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String code;

    private String retriever;

    private BigInteger expiration;

}
