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
 * 任务需求实体类，对应数据库表 questrequirements。
 * 存储任务前置条件配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("questrequirements")
public class QuestrequirementsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * questrequirementid
     */
    private Long questrequirementid;

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
