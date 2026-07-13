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
 * 论坛帖子实体类，对应数据库表 bbs_threads。
 * 存储家族论坛帖子主题。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bbs_threads")
public class BbsThreadsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 帖子ID
     */
    private Long threadid;

    /**
     * 发帖者角色ID
     */
    private Long postercid;

    /**
     * 名称
     */
    private String name;

    /**
     * 时间戳
     */
    private BigInteger timestamp;

    /**
     * 图标类型
     */
    private Integer icon;

    /**
     * 回复数量
     */
    private Integer replycount;

    /**
     * 正文内容
     */
    private String startpost;

    /**
     * 家族ID
     */
    private Long guildid;

    /**
     * 本地帖子ID
     */
    private Long localthreadid;

}
