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
 * 数据库表 `keymap` 的实体类（DO）。
 * <p>
 * 角色按键映射表，持久化技能、物品与动作栏的键盘绑定配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("keymap")
public class KeymapDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Integer key;

    private Integer type;

    private Integer action;

}
