package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.util.I18nUtil;
import org.gms.util.JwtUtils;
import org.gms.dao.entity.AccountsDO;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 提供用户登录认证、JWT令牌生成和刷新功能
 */
@Service
@AllArgsConstructor
public class AuthService {
    private final AccountService accountService;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录获取令牌
     * @param name 账号名称
     * @param password 密码
     * @return 包含JWT token的Map
     */
    public Map<String, String> getToken(String name, String password) {
        AccountsDO account = accountService.findByName(name);
        RequireUtil.requireFalse(account == null || !accountService.checkPassword(password, account),
                I18nUtil.getExceptionMessage("AuthService.account.or.password.error"));

        HashMap<String, String> result = new HashMap<>();
        result.put("token", jwtUtils.generateJwtToken(account.getName()));
        return result;
    }

    /**
     * 刷新JWT令牌
     * @param token 旧的Bearer token
     * @return 包含新JWT token的Map，如果token无效则返回null
     */
    public Map<String, String> refreshToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            AccountsDO account = accountService.findByName(username);
            if (account == null) return null;
            HashMap<String, String> result = new HashMap<>();
            result.put("token", jwtUtils.generateJwtToken(account.getName()));
            return result;
        }
        return null;
    }
}
