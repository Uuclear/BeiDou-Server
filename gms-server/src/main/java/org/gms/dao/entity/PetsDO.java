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
 * 宠物实体类，对应数据库表 pets。
 * 存储角色宠物详细信息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("pets")
public class PetsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * petid
     */
    private Long petid;

    /**
     * 名称
     */
    private String name;

    /**
     * 等级
     */
    private Long level;

    /**
     * closeness
     */
    private Long closeness;

    /**
     * fullness
     */
    private Long fullness;

    /**
     * summoned
     */
    private Boolean summoned;

    /**
     * flag
     */
    private Long flag;

}
