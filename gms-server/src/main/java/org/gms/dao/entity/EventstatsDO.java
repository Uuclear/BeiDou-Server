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
 * 数据库表 `eventstats` 的实体类（DO）。
 * <p>
 * 活动统计表，记录限时活动的参与次数、积分等统计数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("eventstats")
public class EventstatsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long characterid;

    /**
     * 0
     */
    private String name;

    private Integer info;

}
