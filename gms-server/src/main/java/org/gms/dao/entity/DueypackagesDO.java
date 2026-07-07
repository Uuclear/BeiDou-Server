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
 * 数据库表 `dueypackages` 的实体类（DO）。
 * <p>
 * Duey 快递包裹主表，描述寄送给角色的邮件包裹元信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("dueypackages")
public class DueypackagesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long packageid;

    private Long receiverid;

    private String sendername;

    private Long mesos;

    private Timestamp timestamp;

    private String message;

    private Integer checked;

    private Integer type;

}
