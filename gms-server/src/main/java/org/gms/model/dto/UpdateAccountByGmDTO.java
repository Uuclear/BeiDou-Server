package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * GM更新账号信息请求DTO
 * 用于GM后台修改账号信息的请求参数，包含更多管理权限字段
 */
@Data
public class UpdateAccountByGmDTO implements Serializable {
    /**
     * 新密码
     */
    private String newPwd;

    /**
     * PIN码（二级密码）
     */
    private String pin;

    /**
     * PIC码（安全码）
     */
    private String pic;

    /**
     * 出生日期，格式：yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * NX积分（抵用券）
     */
    @Column("nxCredit")
    private Integer nxCredit;

    /**
     * 点券
     */
    @Column("maplePoint")
    private Integer maplePoint;

    /**
     * 预存NX点
     */
    @Column("nxPrepaid")
    private Integer nxPrepaid;

    /**
     * 角色槽位数
     */
    private Integer characterslots;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * Web管理员权限等级
     */
    private Integer webadmin;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 是否禁言
     */
    private Integer mute;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 奖励点数
     */
    private Integer rewardpoints;

    /**
     * 投票点数
     */
    private Integer votepoints;

    /**
     * 语言设置
     */
    private Integer language;
}
