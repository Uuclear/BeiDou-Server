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
 * 玩家NPC装备实体类，对应数据库表 playernpcs_equip。
 * 存储玩家NPC装备配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playernpcs_equip")
public class PlayernpcsEquipDO implements Serializable {

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
     * npcid
     */
    private Integer npcid;

    /**
     * equipid
     */
    private Integer equipid;

    /**
     * 类型
     */
    private Integer type;

    /**
     * equippos
     */
    private Short equippos;

}
