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
 * 数据库表 `responses` 的实体类（DO）。
 * <p>
 * GM 自动回复/工单回复记录，用于客服或管理端的应答模板与历史。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("responses")
public class ResponsesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String chat;

    private String response;

    @Id(keyType = KeyType.Auto)
    private Long id;

}
