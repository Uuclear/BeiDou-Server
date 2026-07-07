package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `reports` 的实体类（DO）。
 * <p>
 * 玩家举报记录表，保存被举报者、举报原因及处理状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reports")
public class ReportsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Timestamp reporttime;

    private Integer reporterid;

    private Integer victimid;

    private Integer reason;

    private String chatlog;

    private String description;

}
