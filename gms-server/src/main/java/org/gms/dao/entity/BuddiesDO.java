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
 * 好友关系实体类，对应数据库表 buddies。
 * 存储角色好友关系及分组。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("buddies")
public class BuddiesDO implements Serializable {

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
     * 好友角色ID
     */
    private Integer buddyid;

    /**
     * 待确认状态
     */
    private Integer pending;

    /**
     * 分组名称
     */
    private String group;

}
