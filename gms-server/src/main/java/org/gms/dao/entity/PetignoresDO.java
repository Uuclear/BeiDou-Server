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
 * 数据库表 `petignores` 的实体类（DO）。
 * <p>
 * 宠物忽略列表，记录宠物不拾取特定掉落物或道具的配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("petignores")
public class PetignoresDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer petid;

    private Integer itemid;

}
