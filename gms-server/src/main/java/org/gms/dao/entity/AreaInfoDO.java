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
 * 数据库表 `area_info` 的实体类（DO）。
 * <p>
 * 区域探索信息表，记录角色对各地图区域的探索完成度。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("area_info")
public class AreaInfoDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer charid;

    private Integer area;

    private String info;

}
