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
 * 数据库表 `playernpcs_field` 的实体类（DO）。
 * <p>
 * 玩家 NPC 外形字段表，保存脸型、发型、肤色等外观属性。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playernpcs_field")
public class PlayernpcsFieldDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer world;

    private Integer map;

    private Integer step;

    private Integer podium;

}
