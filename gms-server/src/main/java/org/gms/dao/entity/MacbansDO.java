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
 * 数据库表 `macbans` 的实体类（DO）。
 * <p>
 * MAC 地址封禁表，记录因违规被封禁的硬件 MAC 及封禁期限。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("macbans")
public class MacbansDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long macbanid;

    private String mac;

    private String aid;

}
