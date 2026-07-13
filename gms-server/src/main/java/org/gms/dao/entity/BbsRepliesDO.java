package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 论坛回复实体类，对应数据库表 bbs_replies。
 * 存储家族论坛帖子回复。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bbs_replies")
public class BbsRepliesDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 回复ID
     */
    private Long replyid;

    /**
     * 帖子ID
     */
    private Long threadid;

    /**
     * 发帖者角色ID
     */
    private Long postercid;

    /**
     * 时间戳
     */
    private BigInteger timestamp;

    /**
     * 内容文本
     */
    private String content;

}
