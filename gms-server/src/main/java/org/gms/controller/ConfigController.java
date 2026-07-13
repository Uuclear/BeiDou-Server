package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GameConfigDO;
import org.gms.model.dto.ConfigTypeDTO;
import org.gms.model.dto.GameConfigReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.ConfigService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 游戏配置管理控制器
 * 提供游戏服务器配置相关的Web API接口，包括配置查询、增删改、YML导入导出等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/config")
public class ConfigController {
    /**
     * 配置服务，处理游戏配置的CRUD和导入导出逻辑
     */
    private final ConfigService configService;

    /**
     * 获取配置参数的大类和类型列表
     * @return 配置类型DTO
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "获取参数大类和参数类型")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigTypeList")
    public ResultBody<ConfigTypeDTO> getConfigTypeList() {
        return ResultBody.success(configService.getConfigTypeList());
    }

    /**
     * 分页获取配置参数列表
     * @param request 包含分页参数和查询条件的请求体
     * @return 分页配置列表
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取参数列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<Page<GameConfigDO>> getConfigList(@RequestBody SubmitBody<GameConfigReqDTO> request) {
        return ResultBody.success(request, configService.getConfigList(request.getData()));
    }

    /**
     * 新增游戏配置参数
     * @param request 包含新配置信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "新增参数")
    @PostMapping("/" + ApiConstant.LATEST + "/addConfig")
    public ResultBody<Object> addConfig(@RequestBody SubmitBody<GameConfigDO> request) {
        configService.addConfig(request.getData());
        return ResultBody.success(request, null);
    }

    /**
     * 修改已有的游戏配置参数
     * @param request 包含更新配置信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "修改参数")
    @PostMapping("/" + ApiConstant.LATEST + "/updateConfig")
    public ResultBody<Object> updateConfig(@RequestBody SubmitBody<GameConfigDO> request) {
        configService.updateConfig(request.getData());
        return ResultBody.success(request, null);
    }

    /**
     * 删除单个配置参数
     * @param id 要删除的配置ID
     * @return 操作成功结果
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "删除参数")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        configService.deleteConfig(id);
        return ResultBody.success(null);
    }

    /**
     * 批量删除配置参数
     * @param request 包含要删除的配置ID列表的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除参数")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteConfigList")
    public ResultBody<Object> deleteConfigList(@RequestBody SubmitBody<List<Long>> request) {
        configService.deleteConfigList(request.getData());
        return ResultBody.success(null);
    }

    /**
     * 从YML文件导入游戏配置
     * @param file 上传的YML配置文件
     * @return 导入结果
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "从yml导入参数")
    @PostMapping(value = "/" + ApiConstant.LATEST + "/importYml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultBody<Object> importYml(@RequestParam("file") MultipartFile file) {
        return ResultBody.success(configService.importYml(file));
    }

    /**
     * 导出游戏配置为YML文件供下载
     * @return YML文件响应实体
     */
    @Tag(name = "/config/" + ApiConstant.LATEST)
    @Operation(summary = "导出配置为yml文件")
    @GetMapping("/" + ApiConstant.LATEST + "/exportYml")
    public ResponseEntity<Resource> exportYml() {
        return configService.exportYml();
    }
}
