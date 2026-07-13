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
 * 制作创建数据实体类，对应数据库表 makercreatedata。
 * Maker系统创建物品配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makercreatedata")
public class MakercreatedataDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 唯一ID
     */
    private Integer id;

    @Id
    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * reqLevel
     */
    private Integer reqLevel;

    /**
     * reqMakerLevel
     */
    private Integer reqMakerLevel;

    /**
     * reqMeso
     */
    private Integer reqMeso;

    /**
     * reqItem
     */
    private Integer reqItem;

    /**
     * reqEquip
     */
    private Integer reqEquip;

    /**
     * catalyst
     */
    private Integer catalyst;

    /**
     * quantity
     */
    private Integer quantity;

    /**
     * tuc
     */
    private Integer tuc;

}
