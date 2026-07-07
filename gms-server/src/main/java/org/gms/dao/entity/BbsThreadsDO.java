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
 * 数据库表 `bbs_threads` 的实体类（DO）。
 * <p>
 * 公告板主题帖表，存储公会或联盟 BBS 的发帖标题与内容。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bbs_threads")
public class BbsThreadsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long threadid;

    private Long postercid;

    private String name;

    private BigInteger timestamp;

    private Integer icon;

    private Integer replycount;

    private String startpost;

    private Long guildid;

    private Long localthreadid;

}
