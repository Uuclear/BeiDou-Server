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
 * 数据库表 `hwidaccounts` 的实体类（DO）。
 * <p>
 * 账号硬件绑定表，关联游戏账号与登录设备的 HWID 指纹。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hwidaccounts")
public class HwidaccountsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer accountid;

    @Id
    private String hwid;

    private Integer relevance;

    private Timestamp expiresat;

}
