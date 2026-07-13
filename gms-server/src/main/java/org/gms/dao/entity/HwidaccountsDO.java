package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * HWID账号关联实体类，对应数据库表 hwidaccounts。
 * 存储硬件ID与账号绑定。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hwidaccounts")
public class HwidaccountsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 账号ID
     */
    private Integer accountid;

    @Id
    /**
     * 硬件ID
     */
    private String hwid;

    /**
     * relevance
     */
    private Integer relevance;

    /**
     * expiresat
     */
    private Timestamp expiresat;

}
