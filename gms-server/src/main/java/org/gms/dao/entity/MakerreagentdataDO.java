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
 * 制作试剂数据实体类，对应数据库表 makerreagentdata。
 * Maker系统试剂材料配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makerreagentdata")
public class MakerreagentdataDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 物品ID
     */
    private Integer itemid;

    /**
     * stat
     */
    private String stat;

    /**
     * value
     */
    private Integer value;

}
