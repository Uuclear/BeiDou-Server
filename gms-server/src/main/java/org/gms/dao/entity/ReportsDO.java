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
 * 举报记录实体类，对应数据库表 reports。
 * 存储玩家举报记录。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("reports")
public class ReportsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Long id;

    /**
     * reporttime
     */
    private Timestamp reporttime;

    /**
     * reporterid
     */
    private Integer reporterid;

    /**
     * victimid
     */
    private Integer victimid;

    /**
     * reason
     */
    private Integer reason;

    /**
     * chatlog
     */
    private String chatlog;

    /**
     * 描述说明
     */
    private String description;

}
