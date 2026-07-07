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
 * 数据库表 `marriages` 的实体类（DO）。
 * <p>
 * 婚姻系统表，存储结婚双方角色 ID、婚礼状态及戒指信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("marriages")
public class MarriagesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long marriageid;

    private Long husbandid;

    private Long wifeid;

}
