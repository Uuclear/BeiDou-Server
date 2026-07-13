package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
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
 * 家族实体类，对应数据库表 guilds。
 * 存储游戏家族信息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("guilds")
public class GuildsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 家族ID
     */
    private Long guildid;

    /**
     * leader
     */
    private Long leader;

    /**
     * gp
     */
    private Long gp;

    /**
     * logo
     */
    private Long logo;

    @Column("logoColor")
    /**
     * logoColor
     */
    private Integer logoColor;

    /**
     * 名称
     */
    private String name;

    /**
     * rank1title
     */
    private String rank1title;

    /**
     * rank2title
     */
    private String rank2title;

    /**
     * rank3title
     */
    private String rank3title;

    /**
     * rank4title
     */
    private String rank4title;

    /**
     * rank5title
     */
    private String rank5title;

    /**
     * 容量上限
     */
    private Long capacity;

    @Column("logoBG")
    /**
     * logoBG
     */
    private Long logoBG;

    @Column("logoBGColor")
    /**
     * logoBGColor
     */
    private Integer logoBGColor;

    /**
     * 公告内容
     */
    private String notice;

    /**
     * signature
     */
    private Integer signature;

    @Column("allianceId")
    /**
     * allianceId
     */
    private Long allianceId;

}
