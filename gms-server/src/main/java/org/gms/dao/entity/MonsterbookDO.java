package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 怪物手册实体类，对应数据库表 monsterbook。
 * 存储怪物手册收集进度。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("monsterbook")
public class MonsterbookDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 角色ID
     */
    private Integer charid;

    @Id
    /**
     * cardid
     */
    private Integer cardid;

    /**
     * 等级
     */
    private Integer level;

}
