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
 * 数据库表 `playerdiseases` 的实体类（DO）。
 * <p>
 * 角色异常状态表，记录中毒、诅咒等负面状态及其剩余持续时间。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("playerdiseases")
public class PlayerdiseasesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer charid;

    private Integer disease;

    private Integer mobskillid;

    private Integer mobskilllv;

    private Long length;

}
