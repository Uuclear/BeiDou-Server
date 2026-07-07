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
 * 数据库表 `ipbans` 的实体类（DO）。
 * <p>
 * IP 封禁表，记录被封禁的 IP 地址及封禁原因与有效期。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ipbans")
public class IpbansDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long ipbanid;

    private String ip;

    private String aid;

}
