package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 数据库表 `makerreagentdata` 的实体类（DO）。
 * <p>
 * 制作人催化剂表，定义可用于制作的催化剂道具及效果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makerreagentdata")
public class MakerreagentdataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer itemid;

    private String stat;

    private Integer value;

}
