package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AutobanConfigDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AutobanConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自动封禁配置控制器
 * 提供自动封禁规则配置的查询和更新Web API接口，
 * 用于管理游戏中的反作弊自动封禁策略
 *
 * @author Nap
 * @since 2026-04-22
 */
@RestController
@AllArgsConstructor
@RequestMapping("/autoban")
public class AutobanConfigController {
    /**
     * 自动封禁配置服务，处理配置的加载、查询和更新逻辑
     */
    private final AutobanConfigService autobanConfigService;

    /**
     * 获取自动封禁配置列表
     * 包含所有自动封禁类型的默认值和当前配置值
     * @return 自动封禁配置DTO列表
     */
    @Tag(name = "/autoban/" + ApiConstant.LATEST)
    @Operation(summary = "获取自动封禁配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<AutobanConfigDTO>> getConfigList() {
        return ResultBody.success(autobanConfigService.getConfigList());
    }

    /**
     * 更新指定类型的自动封禁配置
     * 可以修改封禁点数、过期时间、是否禁用、描述等
     * @param request 包含配置更新信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/autoban/" + ApiConstant.LATEST)
    @Operation(summary = "更新自动封禁配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateConfig")
    public ResultBody<Object> updateConfig(@RequestBody SubmitBody<AutobanConfigDTO> request) {
        autobanConfigService.updateConfig(request.getData());
        return ResultBody.success(request, null);
    }
}
