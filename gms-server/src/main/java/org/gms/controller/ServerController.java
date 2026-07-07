package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.constants.net.ServerConstants;
import org.gms.model.dto.ChannelListRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.ServerShutdownDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.net.server.Server;
import org.gms.service.ServerService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 游戏服务器生命周期控制器，提供启停、重启、状态查询及大区/频道列表接口。
 * 直接调用 Server 单例与 ServerService，属于运维级 REST 入口。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/server")
public class ServerController {
    private final ApplicationContext applicationContext;
    private final ServerService serverService;

    /**
     * 停止所有。
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "停止所有")
    @GetMapping("/" + ApiConstant.LATEST + "/shutdown")
    public void shutdown() {
        // 这里只能触发destroy，但服务不能正常停止
        SpringApplication.exit(applicationContext);
        // 这里才能正常的停止
        System.exit(0);
    }

    /**
     * 停止服务。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "停止服务")
    @GetMapping("/" + ApiConstant.LATEST + "/stopServer")
    public ResultBody<Object> stopServer() {
        Server.getInstance().shutdownInternal(false);
        return ResultBody.success();
    }

    /**
     * 自定义停止服务。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "自定义停止服务")
    @PostMapping("/" + ApiConstant.LATEST + "/stopServerWithMsgAndInternal")
    public ResultBody<Object> stopServerWithMsgAndInternal(
            @Parameter(
                    name = "stopConfigData", in = ParameterIn.DEFAULT, required = true,
                    description = "停服请求参数：包含停服自定义消息，停服倒计时(单位：分钟)"
            )
            @RequestBody SubmitBody<ServerShutdownDTO> request) {
        System.out.println(request.getData());
        Server.getInstance().shutdownWithMsgAndInternal(request.getData());
        return ResultBody.success();
    }

    /**
     * 启动服务。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "启动服务")
    @GetMapping("/" + ApiConstant.LATEST + "/startServer")
    public ResultBody<Object> startServer() {
        Server.getInstance().init();
        return ResultBody.success();
    }

    /**
     * 重启服务。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "重启服务")
    @GetMapping("/" + ApiConstant.LATEST + "/restartServer")
    public ResultBody<Object> restartServer() {
        Server.getInstance().shutdownInternal(true);
        return ResultBody.success();
    }

    /**
     * 查询服务状态。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "查询服务状态")
    @GetMapping("/" + ApiConstant.LATEST + "/online")
    public ResultBody<Boolean> online() {
        return ResultBody.success(Server.getInstance().isOnline());
    }

    /**
     * 大区列表。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "大区列表")
    @GetMapping("/" + ApiConstant.LATEST + "/world/list")
    public ResultBody<Object> worldList() {
        return ResultBody.success(serverService.worldList());
    }

    /**
     * 频道列表。
     *
     * @param worldId 大区（世界）ID
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "频道列表")
    @GetMapping("/" + ApiConstant.LATEST + "/channel/list")
    public ResultBody<List<ChannelListRtnDTO>> channelList(@RequestParam int worldId) {
        return ResultBody.success(serverService.channelList(worldId));
    }

    /**
     * 查询版本号。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "查询版本号")
    @GetMapping("/" + ApiConstant.LATEST + "/version")
    public ResultBody<String> version() {
        return ResultBody.success(ServerConstants.BEI_DOU_VERSION);
    }
}
