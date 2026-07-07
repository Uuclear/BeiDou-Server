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
 * 数据库表 `questprogress` 的实体类（DO）。
 * <p>
 * 任务进度表，存储任务进行中的计数器、收集进度等中间状态。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questprogress")
public class QuestprogressDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterid;

    private Long queststatusid;

    private Integer progressid;

    private String progress;

}
