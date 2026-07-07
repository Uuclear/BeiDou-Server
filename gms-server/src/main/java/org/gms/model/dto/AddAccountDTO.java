package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 新增游戏账号请求 DTO，供管理端创建账号接口使用。主要字段：登录名 name、密码 password、生日 birthday、语言 language。
 */
@Data
public class AddAccountDTO implements Serializable {
    private String name;
    private String password;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private Integer language;
}
