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
 * 数据库表 `questrequirements` 的实体类（DO）。
 * <p>
 * 任务需求条件表，定义完成任务所需的前置任务、物品或等级等条件。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questrequirements")
public class QuestrequirementsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long questrequirementid;

    private Integer questid;

    private Integer status;

    private byte[] data;

}
