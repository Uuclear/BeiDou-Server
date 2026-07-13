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
 * 活动统计实体类，对应数据库表 eventstats。
 * 存储游戏活动统计数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("eventstats")
public class EventstatsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 角色ID
     */
    private Long characterid;

    /**
     * 0
     */
    private String name;

    /**
     * 信息内容
     */
    private Integer info;

}
