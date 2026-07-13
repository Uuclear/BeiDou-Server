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
 * 服务器管理控制器
 * 提供游戏服务器管理的Web API接口，包括服务器启动/停止/重启、状态查询、大区频道列表、版本查询等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/server")
public class ServerController {
    /**
     * Spring应用上下文，用于退出Spring应用
     */
    private final ApplicationContext applicationContext;

    /**
     * 服务器服务，处理大区和频道列表查询逻辑
     */
    private final ServerService serverService;

    /**
     * 强制停止整个应用（包括Web服务器和游戏服务器）
     * 先触发Spring上下文销毁，再调用System.exit确保JVM退出
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "停止所有")
    @GetMapping("/" + ApiConstant.LATEST + "/shutdown")
    public void shutdown() {
        SpringApplication.exit(applicationContext);
        System.exit(0);
    }

    /**
     * 停止游戏服务器（不停止Web管理后台）
     * @return 操作成功结果
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "停止服务")
    @GetMapping("/" + ApiConstant.LATEST + "/stopServer")
    public ResultBody<Object> stopServer() {
        Server.getInstance().shutdownInternal(false);
        return ResultBody.success();
    }

    /**
     * 自定义停止游戏服务器，支持停服消息和倒计时
     * @param request 包含停服自定义消息和倒计时（分钟）的请求体
     * @return 操作成功结果
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
     * 启动游戏服务器
     * @return 操作成功结果
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "启动服务")
    @GetMapping("/" + ApiConstant.LATEST + "/startServer")
    public ResultBody<Object> startServer() {
        Server.getInstance().init();
        return ResultBody.success();
    }

    /**
     * 重启游戏服务器
     * @return 操作成功结果
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "重启服务")
    @GetMapping("/" + ApiConstant.LATEST + "/restartServer")
    public ResultBody<Object> restartServer() {
        Server.getInstance().shutdownInternal(true);
        return ResultBody.success();
    }

    /**
     * 查询游戏服务器在线状态
     * @return 服务器是否在线
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "查询服务状态")
    @GetMapping("/" + ApiConstant.LATEST + "/online")
    public ResultBody<Boolean> online() {
        return ResultBody.success(Server.getInstance().isOnline());
    }

    /**
     * 获取所有大区（世界）列表
     * @return 大区列表
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "大区列表")
    @GetMapping("/" + ApiConstant.LATEST + "/world/list")
    public ResultBody<Object> worldList() {
        return ResultBody.success(serverService.worldList());
    }

    /**
     * 获取指定大区下的所有频道列表
     * @param worldId 大区ID
     * @return 频道列表
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "频道列表")
    @GetMapping("/" + ApiConstant.LATEST + "/channel/list")
    public ResultBody<List<ChannelListRtnDTO>> channelList(@RequestParam int worldId) {
        return ResultBody.success(serverService.channelList(worldId));
    }

    /**
     * 查询服务器版本号
     * @return 北斗版本号
     */
    @Tag(name = "/server/" + ApiConstant.LATEST)
    @Operation(summary = "查询版本号")
    @GetMapping("/" + ApiConstant.LATEST + "/version")
    public ResultBody<String> version() {
        return ResultBody.success(ServerConstants.BEI_DOU_VERSION);
    }
}
