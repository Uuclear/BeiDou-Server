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
 * 数据库表 `newyear` 的实体类（DO）。
 * <p>
 * 新年贺卡活动表，记录发送方、接收方、留言内容及收发时间。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("newyear")
public class NewyearDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer senderid;

    private String sendername;

    private Integer receiverid;

    private String receivername;

    private String message;

    private Boolean senderdiscard;

    private Boolean receiverdiscard;

    private Boolean received;

    private Long timesent;

    private Long timereceived;

}
