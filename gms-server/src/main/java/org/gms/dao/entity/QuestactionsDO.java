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
 * 数据库表 `questactions` 的实体类（DO）。
 * <p>
 * 任务动作数据表，保存任务脚本触发的奖励、传送等动作配置。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questactions")
public class QuestactionsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long questactionid;

    private Integer questid;

    private Integer status;

    private byte[] data;

}
