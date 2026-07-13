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
 * 仓库实体类，对应数据库表 storages。
 * 存储角色仓库物品数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("storages")
public class StoragesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * storageid
     */
    private Long storageid;

    /**
     * 账号ID
     */
    private Integer accountid;

    /**
     * 服务器世界ID
     */
    private Integer world;

    /**
     * slots
     */
    private Integer slots;

    /**
     * 金币数量
     */
    private Integer meso;

}
