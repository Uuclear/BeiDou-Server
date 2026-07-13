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
 * 游戏生命实体类，对应数据库表 plife。
 * 存储地图生命体配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("plife")
public class PlifeDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Long id;

    /**
     * 服务器世界ID
     */
    private Integer world;

    /**
     * 当前地图ID
     */
    private Integer map;

    /**
     * life
     */
    private Integer life;

    /**
     * 类型
     */
    private String type;

    /**
     * cy
     */
    private Integer cy;

    /**
     * f
     */
    private Integer f;

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
     * x
     */
    private Integer x;

    /**
     * y
     */
    private Integer y;

    /**
     * hide
     */
    private Integer hide;

    /**
     * mobtime
     */
    private Integer mobtime;

    /**
     * team
     */
    private Integer team;

}
