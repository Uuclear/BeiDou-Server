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
 * 自动封禁配置控制器，提供反作弊自动封禁规则的查询与更新接口。
 * 作为 GM 后台与 AutobanConfigService 之间的 REST 适配层。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/autoban")
public class AutobanConfigController {
    private final AutobanConfigService autobanConfigService;

    /**
     * 获取全部自动封禁配置列表。
     * @return 配置 DTO 列表
     */
    @Tag(name = "/autoban/" + ApiConstant.LATEST)
    @Operation(summary = "获取自动封禁配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<AutobanConfigDTO>> getConfigList() {
        return ResultBody.success(autobanConfigService.getConfigList());
    }

    /**
     * 更新自动封禁配置。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/autoban/" + ApiConstant.LATEST)
    @Operation(summary = "更新自动封禁配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateConfig")
    public ResultBody<Object> updateConfig(@RequestBody SubmitBody<AutobanConfigDTO> request) {
        autobanConfigService.updateConfig(request.getData());
        return ResultBody.success(request, null);
    }
}
