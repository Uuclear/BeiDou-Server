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
 * 数据库表 `cooldowns` 的实体类（DO）。
 * <p>
 * 冷却时间表，记录技能、物品或活动的剩余冷却毫秒数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("cooldowns")
public class CooldownsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer charid;

    private Integer skillid;

    private Long length;

    private Long starttime;

}
