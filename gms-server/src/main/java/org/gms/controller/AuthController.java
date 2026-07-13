package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 * 提供用户登录、登出、JWT令牌刷新等认证相关的Web API接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    /**
     * 认证服务，处理登录认证和令牌管理逻辑
     */
    private final AuthService authService;

    /**
     * 构造函数，通过依赖注入注入认证服务
     * @param authService 认证服务
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口
     * 验证用户名和密码，成功后返回JWT令牌
     * @param data 包含用户名和密码的请求体
     * @return 包含JWT token的响应
     */
    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "登录")
    @PostMapping("/" + ApiConstant.LATEST + "/login")
    public ResultBody<Map<String, String>> login(@RequestBody SubmitBody<Map<String, String>> data) {
        return ResultBody.success(authService.getToken(data.getData().get("username"), data.getData().get("password")));
    }

    /**
     * 用户登出接口
     * 客户端清除token即可，服务端无状态
     * @return 操作成功结果
     */
    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "登出")
    @DeleteMapping("/" + ApiConstant.LATEST + "/logout")
    public ResultBody<Object> logout() {
        return ResultBody.success();
    }

    /**
     * 刷新JWT令牌接口
     * 使用旧的有效令牌换取新令牌
     * @param token Authorization请求头中的Bearer token
     * @return 包含新JWT token的响应，如果token无效则返回null
     */
    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "刷新token")
    @GetMapping("/" + ApiConstant.LATEST + "/refreshToken")
    public ResultBody<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        return ResultBody.success(authService.refreshToken(token));
    }
}
