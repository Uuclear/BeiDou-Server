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
 * 技能宏实体类，对应数据库表 skillmacros。
 * 存储角色技能宏配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("skillmacros")
public class SkillmacrosDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * position
     */
    private Integer position;

    /**
     * skill1
     */
    private Integer skill1;

    /**
     * skill2
     */
    private Integer skill2;

    /**
     * skill3
     */
    private Integer skill3;

    /**
     * 名称
     */
    private String name;

    /**
     * shout
     */
    private Integer shout;

}
