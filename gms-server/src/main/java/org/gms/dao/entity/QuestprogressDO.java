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
 * 任务进度实体类，对应数据库表 questprogress。
 * 存储角色任务进度。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questprogress")
public class QuestprogressDO implements Serializable {

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
     * 角色ID
     */
    private Integer characterid;

    /**
     * queststatusid
     */
    private Long queststatusid;

    /**
     * progressid
     */
    private Integer progressid;

    /**
     * progress
     */
    private String progress;

}
