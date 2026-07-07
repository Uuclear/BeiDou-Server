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
 * 数据库表 `hp_mp_alert` 的实体类（DO）。
 * <p>
 * HP/MP 低值提醒配置表，定义角色血量/蓝量告警阈值。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hp_mp_alert")
public class HpMpAlertDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer cId;

    private Byte hp;

    private Byte mp;

}
