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
 * 角色异常状态实体类，对应数据库表 playerdiseases。
 * 存储角色异常状态效果。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playerdiseases")
public class PlayerdiseasesDO implements Serializable {

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
    private Integer charid;

    /**
     * disease
     */
    private Integer disease;

    /**
     * mobskillid
     */
    private Integer mobskillid;

    /**
     * mobskilllv
     */
    private Integer mobskilllv;

    /**
     * 持续时长毫秒
     */
    private Long length;

}
