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
 * 数据库表 `savedlocations` 的实体类（DO）。
 * <p>
 * 角色保存传送点表，记录自定义传送石/传送门的目标地图与坐标。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("savedlocations")
public class SavedlocationsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private String locationtype;

    private Integer map;

    private Integer portal;

}
