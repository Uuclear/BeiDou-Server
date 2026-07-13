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
 * 玩家NPC地图实体类，对应数据库表 playernpcs_field。
 * 存储玩家NPC地图部署。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playernpcs_field")
public class PlayernpcsFieldDO implements Serializable {

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
     * 服务器世界ID
     */
    private Integer world;

    /**
     * 当前地图ID
     */
    private Integer map;

    /**
     * step
     */
    private Integer step;

    /**
     * podium
     */
    private Integer podium;

}
