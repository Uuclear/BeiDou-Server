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
 * 任务状态实体类，对应数据库表 queststatus。
 * 存储角色任务状态。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("queststatus")
public class QueststatusDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * queststatusid
     */
    private Long queststatusid;

    /**
     * 角色ID
     */
    private Integer characterid;

    /**
     * quest
     */
    private Integer quest;

    /**
     * status
     */
    private Integer status;

    /**
     * time
     */
    private Integer time;

    /**
     * expires
     */
    private Long expires;

    /**
     * forfeited
     */
    private Integer forfeited;

    /**
     * completed
     */
    private Integer completed;

    /**
     * 信息内容
     */
    private Integer info;

}
