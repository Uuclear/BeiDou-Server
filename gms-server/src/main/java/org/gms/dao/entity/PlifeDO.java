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
 * 数据库表 `plife` 的实体类（DO）。
 * <p>
 * 玩家生活技能（种植）数据表，记录植物生长阶段、收获时间等 plife 状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("plife")
public class PlifeDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer world;

    private Integer map;

    private Integer life;

    private String type;

    private Integer cy;

    private Integer f;

    private Integer fh;

    private Integer rx0;

    private Integer rx1;

    private Integer x;

    private Integer y;

    private Integer hide;

    private Integer mobtime;

    private Integer team;

}
