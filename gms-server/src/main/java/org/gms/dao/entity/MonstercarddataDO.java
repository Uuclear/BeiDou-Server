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
 * 数据库表 `monstercarddata` 的实体类（DO）。
 * <p>
 * 怪物卡数据定义表，描述怪物卡对应的怪物 ID 与卡片属性。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("monstercarddata")
public class MonstercarddataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer cardid;

    private Integer mobid;

}
