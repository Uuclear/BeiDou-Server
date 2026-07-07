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
 * 数据库表 `makerrecipedata` 的实体类（DO）。
 * <p>
 * 制作人配方表，描述合成所需材料、催化剂及产出物品。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makerrecipedata")
public class MakerrecipedataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer itemid;

    @Id
    private Integer reqItem;

    private Integer count;

}
