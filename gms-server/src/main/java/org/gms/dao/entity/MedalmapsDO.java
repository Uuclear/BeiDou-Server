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
 * 数据库表 `medalmaps` 的实体类（DO）。
 * <p>
 * 勋章成就地图关联表，定义勋章任务与可完成地图的对应关系。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("medalmaps")
public class MedalmapsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Long queststatusid;

    private Integer mapid;

}
