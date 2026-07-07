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
 * 数据库表 `skillmacros` 的实体类（DO）。
 * <p>
 * 技能宏配置表，保存快捷栏中组合技能的名称、图标与技能序列。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("skillmacros")
public class SkillmacrosDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Integer position;

    private Integer skill1;

    private Integer skill2;

    private Integer skill3;

    private String name;

    private Integer shout;

}
