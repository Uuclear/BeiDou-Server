package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 用户更新账号信息请求DTO
 * 用于普通用户修改自己账号信息的请求参数
 */
@Data
public class UpdateAccountByUserDTO implements Serializable {
    /**
     * 旧密码（验证用）
     */
    private String oldPwd;

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
     * 昵称
     */
    private String nick;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 语言设置
     */
    private Integer language;
}
