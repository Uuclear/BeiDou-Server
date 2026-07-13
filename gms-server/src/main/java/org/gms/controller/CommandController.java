package org.gms.controller;


import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CommandInfoDO;
import org.gms.model.dto.*;
import org.gms.service.CommandService;
import org.springframework.web.bind.annotation.*;

/**
 * GM命令管理控制器
 * 提供GM命令相关的Web API接口，包括命令库查询、命令状态更新、服务器资源重载等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/command")
public class CommandController {
    /**
     * 命令服务，处理GM命令管理和服务器重载逻辑
     */
    private final CommandService commandService;

    /**
     * 从数据库分页查询命令库所有指令与状态
     * @param submitBody 包含分页参数和查询条件的请求体
     * @return 分页命令列表
     */
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "查询命令库所有指令与状态")
    @PostMapping("/" + ApiConstant.LATEST + "/getCommandListFromDB")
    public ResultBody<Page<CommandReqDTO>> getCommandListFromDB(@RequestBody SubmitBody<CommandReqDTO> submitBody) {
        return ResultBody.success(commandService.getCommandListFromDB(submitBody.getData()));
    }

    /**
     * 更新命令库中指定指令的状态
     * @param submitBody 包含命令信息的请求体
     * @return 更新后的命令信息
     */
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "更新命令库所有指令与状态")
    @PostMapping("/" + ApiConstant.LATEST + "/updateCommand")
    public ResultBody<CommandInfoDO> updateCommand(@RequestBody SubmitBody<CommandReqDTO> submitBody) {
        return ResultBody.success(commandService.updateCommand(submitBody.getData()));
    }

    /**
     * 复用GM命令代码重载服务器事件脚本
     * 重新加载所有事件脚本，无需重启服务器
     * @return 操作成功结果
     */
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重载事件")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadEventsByGMCommand")
    public ResultBody reloadEventsByGMCommand() {
        commandService.reloadEventsByGMCommand();
        return ResultBody.success();
    }

    /**
     * 复用GM命令代码重装所有传送点
     * 重新加载地图传送点数据，无需重启服务器
     * @return 操作成功结果
     */
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重装传送点")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadPortalsByGMCommand")
    public ResultBody reloadPortalsByGMCommand() {
        commandService.reloadPortalsByGMCommand();
        return ResultBody.success();
    }

    /**
     * 复用GM命令代码重装所有地图
     * 重新加载所有地图数据，并将在线玩家转移到新地图实例
     * @return 操作成功结果
     */
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重装地图")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadMapsByGMCommand")
    public ResultBody reloadMapsByGMCommand() {
        commandService.reloadMapsByGMCommand();
        return ResultBody.success();
    }
}
