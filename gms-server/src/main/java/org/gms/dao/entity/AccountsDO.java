package org.gms.dao.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * 账号信息实体类，对应数据库表 accounts。
 * 存储游戏用户的账号基本信息、登录状态、充值点数等数据。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class AccountsDO implements Serializable {

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
     * 名称
     */
    private String name;

    @JsonIgnore
    /**
     * 密码
     */
    private String password;

    /**
     * 二级密码PIN码
     */
    private String pin;

    /**
     * 角色选择密码PIC码
     */
    private String pic;

    /**
     * 登录状态
     */
    private Integer loggedin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * 最后登录时间
     */
    private Timestamp lastlogin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * 创建时间
     */
    private Timestamp createdat;

    @JsonFormat(pattern = "yyyy-MM-dd")
    /**
     * 生日日期
     */
    private Date birthday;

    /**
     * 是否被封禁
     */
    private Boolean banned;

    /**
     * 封禁原因
     */
    private String banreason;

    /**
     * 绑定MAC地址列表
     */
    private String macs;

    @Column("nxCredit")
    /**
     * NX信用点余额
     */
    private Integer nxCredit;

    @Column("maplePoint")
    /**
     * 枫叶点余额
     */
    private Integer maplePoint;

    @Column("nxPrepaid")
    /**
     * NX预充值点数余额
     */
    private Integer nxPrepaid;

    /**
     * 可创建角色槽位数
     */
    private Integer characterslots;

    /**
     * 性别
     */
    private Integer gender;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    /**
     * 临时封禁到期时间
     */
    private Timestamp tempban;

    /**
     * 封禁原因代码
     */
    private Integer greason;

    /**
     * 是否同意服务条款
     */
    private Boolean tos;

    /**
     * 网站登录标识
     */
    private String sitelogged;

    /**
     * 网站管理员权限等级
     */
    private Integer webadmin;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 禁言状态
     */
    private Integer mute;

    /**
     * 注册邮箱
     */
    private String email;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 奖励积分
     */
    private Integer rewardpoints;

    /**
     * 投票积分
     */
    private Integer votepoints;

    /**
     * 硬件ID
     */
    private String hwid;

    /**
     * 语言设置
     */
    private Integer language;
}
