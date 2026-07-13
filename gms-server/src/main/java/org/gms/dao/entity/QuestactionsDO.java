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
 * 任务动作实体类，对应数据库表 questactions。
 * 存储任务完成动作配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questactions")
public class QuestactionsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * questactionid
     */
    private Long questactionid;

    /**
     * 关联任务ID
     */
    private Integer questid;

    /**
     * status
     */
    private Integer status;

    /**
     * data
     */
    private byte[] data;

}
