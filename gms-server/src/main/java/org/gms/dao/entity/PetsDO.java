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
 * 数据库表 `pets` 的实体类（DO）。
 * <p>
 * 宠物数据表，持久化宠物等级、亲密度、技能、装备及饱食度等信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("pets")
public class PetsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long petid;

    private String name;

    private Long level;

    private Long closeness;

    private Long fullness;

    private Boolean summoned;

    private Long flag;

}
