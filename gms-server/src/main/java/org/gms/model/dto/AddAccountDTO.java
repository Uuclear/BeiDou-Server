package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 添加账号请求DTO
 * 用于GM后台创建新游戏账号时的请求参数
 */
@Data
public class AddAccountDTO implements Serializable {
    /**
     * 账号名称（用户名）
     */
    private String name;

    /**
     * 账号密码
     */
    private String password;

    /**
     * 出生日期，格式：yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 语言设置
     */
    private Integer language;
}
