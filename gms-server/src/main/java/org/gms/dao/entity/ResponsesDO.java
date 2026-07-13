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
 * 响应配置实体类，对应数据库表 responses。
 * 存储系统响应配置。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("responses")
public class ResponsesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * chat
     */
    private String chat;

    /**
     * response
     */
    private String response;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Long id;

}
