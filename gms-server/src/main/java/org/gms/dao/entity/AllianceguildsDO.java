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
 * 数据库表 `allianceguilds` 的实体类（DO）。
 * <p>
 * 联盟-公会关联表，描述联盟下辖公会成员关系。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("allianceguilds")
public class AllianceguildsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer allianceid;

    private Integer guildid;

}
