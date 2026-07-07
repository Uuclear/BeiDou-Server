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
 * 数据库表 `skills` 的实体类（DO）。
 * <p>
 * 角色已学技能表，记录技能 ID、当前等级、主技能等级及过期时间。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("skills")
public class SkillsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer skillid;

    private Integer characterid;

    private Integer skilllevel;

    private Integer masterlevel;

    private Long expiration;

}
