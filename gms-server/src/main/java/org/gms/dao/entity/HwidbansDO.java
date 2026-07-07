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
 * 数据库表 `hwidbans` 的实体类（DO）。
 * <p>
 * 硬件 ID（HWID）封禁表，记录被封禁的设备指纹及封禁期限。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hwidbans")
public class HwidbansDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long hwidbanid;

    private String hwid;

}
