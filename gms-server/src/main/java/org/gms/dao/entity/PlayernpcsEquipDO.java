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
 * 数据库表 `playernpcs_equip` 的实体类（DO）。
 * <p>
 * 玩家 NPC 装备外观表，存储自定义 NPC 穿戴的装备道具 ID。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playernpcs_equip")
public class PlayernpcsEquipDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer npcid;

    private Integer equipid;

    private Integer type;

    private Short equippos;

}
