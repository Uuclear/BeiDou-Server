package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 玩家自助修改账号请求 DTO，用于修改密码、PIN、PIC、昵称、邮箱等个人信息。
 */
@Data
public class UpdateAccountByUserDTO implements Serializable {
    private String oldPwd;
    private String newPwd;
    private String pin;
    private String pic;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String nick;
    private String email;
    private Integer language;
}
