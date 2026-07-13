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
 * 保存位置实体类，对应数据库表 savedlocations。
 * 存储角色保存的位置点。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("savedlocations")
public class SavedlocationsDO implements Serializable {

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
     * 角色ID
     */
    private Integer characterid;

    /**
     * locationtype
     */
    private String locationtype;

    /**
     * 当前地图ID
     */
    private Integer map;

    /**
     * portal
     */
    private Integer portal;

}
