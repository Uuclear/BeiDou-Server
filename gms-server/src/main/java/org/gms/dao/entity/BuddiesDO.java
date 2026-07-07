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
 * 数据库表 `buddies` 的实体类（DO）。
 * <p>
 * 好友列表表，存储角色间的好友关系、分组及备注信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("buddies")
public class BuddiesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Integer buddyid;

    private Integer pending;

    private String group;

}
