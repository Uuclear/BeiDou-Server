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
 * 数据库表 `makercreatedata` 的实体类（DO）。
 * <p>
 * 制作人可制作物品表，列出各职业可制作的装备与道具清单。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makercreatedata")
public class MakercreatedataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Id
    private Integer itemid;

    private Integer reqLevel;

    private Integer reqMakerLevel;

    private Integer reqMeso;

    private Integer reqItem;

    private Integer reqEquip;

    private Integer catalyst;

    private Integer quantity;

    private Integer tuc;

}
