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
 * 玩家NPC实体类，对应数据库表 playernpcs。
 * 存储玩家自定义NPC配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playernpcs")
public class PlayernpcsDO implements Serializable {

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
     * 名称
     */
    private String name;

    /**
     * 发型ID
     */
    private Integer hair;

    /**
     * 脸型ID
     */
    private Integer face;

    /**
     * skin
     */
    private Integer skin;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * x
     */
    private Integer x;

    /**
     * cy
     */
    private Integer cy;

    /**
     * 服务器世界ID
     */
    private Integer world;

    /**
     * 当前地图ID
     */
    private Integer map;

    /**
     * dir
     */
    private Integer dir;

    /**
     * scriptid
     */
    private Integer scriptid;

    /**
     * fh
     */
    private Integer fh;

    /**
     * rx0
     */
    private Integer rx0;

    /**
     * rx1
     */
    private Integer rx1;

    /**
     * worldrank
     */
    private Integer worldrank;

    /**
     * overallrank
     */
    private Integer overallrank;

    /**
     * worldjobrank
     */
    private Integer worldjobrank;

    /**
     * 职业ID
     */
    private Integer job;

}
