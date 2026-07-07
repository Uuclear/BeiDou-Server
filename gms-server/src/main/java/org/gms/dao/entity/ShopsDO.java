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
 * 数据库表 `shops` 的实体类（DO）。
 * <p>
 * NPC 商店定义表，描述商店所属 NPC、地图及商店类型等元信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("shops")
public class ShopsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long shopid;

    private Integer npcid;

}
