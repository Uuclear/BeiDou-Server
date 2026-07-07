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
 * 数据库表 `alliance` 的实体类（DO）。
 * <p>
 * 联盟信息表，存储多个公会组成的联盟名称、等级与公告。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("alliance")
public class AllianceDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String name;

    private Long capacity;

    private String notice;

    private String rank1;

    private String rank2;

    private String rank3;

    private String rank4;

    private String rank5;

}
