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
 * 技能实体类，对应数据库表 skills。
 * 存储角色已学技能及等级。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("skills")
public class SkillsDO implements Serializable {

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
     * 技能ID
     */
    private Integer skillid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * skilllevel
     */
    private Integer skilllevel;

    /**
     * masterlevel
     */
    private Integer masterlevel;

    /**
     * expiration
     */
    private Long expiration;

}
