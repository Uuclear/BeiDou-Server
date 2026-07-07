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
 * 数据库表 `bbs_replies` 的实体类（DO）。
 * <p>
 * 公告板回复帖表，存储主题帖下的回复内容与作者信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bbs_replies")
public class BbsRepliesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long replyid;

    private Long threadid;

    private Long postercid;

    private BigInteger timestamp;

    private String content;

}
