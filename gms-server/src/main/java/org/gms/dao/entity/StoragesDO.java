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
 * 数据库表 `storages` 的实体类（DO）。
 * <p>
 * 仓库物品表，存储角色仓库中非装备类物品的堆叠与位置信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("storages")
public class StoragesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long storageid;

    private Integer accountid;

    private Integer world;

    private Integer slots;

    private Integer meso;

}
