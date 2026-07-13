package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 纸条实体类，对应数据库表 notes。
 * 存储角色间纸条消息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notes")
public class NotesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * to
     */
    private String to;

    /**
     * from
     */
    private String from;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 人气值
     */
    private Integer fame;

    /**
     * deleted
     */
    private Integer deleted;

}
