package org.gms.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.model.pojo.InformationSearch;
import org.gms.model.pojo.InformationResult;
import org.gms.service.CommonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用功能控制器
 * 提供通用的Web API接口，包括装备属性查询、在线玩家统计、游戏资料查询等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/common")
public class CommonController {
    /**
     * 通用服务，处理通用业务逻辑
     */
    private final CommonService commonService;

    /**
     * 根据物品ID查询装备基础属性信息
     * @param submitBody 包含物品ID的请求体
     * @return 装备属性信息
     */
    @Tag(name = "/common/" + ApiConstant.LATEST)
    @Operation(summary = "查询装备基础属性信息")
    @PostMapping("/" + ApiConstant.LATEST + "/getEquipmentInfoByItemId")
    public ResultBody<Object> getEquipmentInfoByItemId(@RequestBody SubmitBody<EquipmentInfoReqDTO> submitBody) {
        return ResultBody.success(commonService.getEquipmentInfoByItemId(submitBody.getData()));
    }

    /**
     * 查询指定世界列表中当前在线的玩家总数
     * @param submitBody 包含世界ID列表的请求体
     * @return 在线玩家总数
     */
    @Tag(name = "/common/" + ApiConstant.LATEST)
    @Operation(summary = "查询所有世界中当前在线玩家数量")
    @PostMapping("/" + ApiConstant.LATEST + "/getAllWorldsOnlinePlayersCount")
    public ResultBody<Integer> getAllWorldsOnlinePlayersCount(@RequestBody SubmitBody<ServerInfoReqDto> submitBody) {
        return ResultBody.success(commonService.getAllWorldsOnlinePlayersCount(submitBody.getData().getWorldIdList()));
    }

    /**
     * 游戏资料通用查询接口
     * 根据ID或名称查询游戏内各种信息，包括物品、怪物、地图、NPC、技能等
     * @param submitBody 包含查询类型和过滤条件的请求体
     * @return 查询结果列表
     */
    @Tag(name = "/common/" + ApiConstant.LATEST)
    @Operation(summary = "资料查询，根据id或者name查询对应信息")
    @PostMapping("/" + ApiConstant.LATEST + "/informationSearch")
    public ResultBody<List<InformationResult>> informationSearch(@RequestBody SubmitBody<InformationSearch> submitBody) {
        return ResultBody.success(commonService.getInformation(submitBody.getData()));
    }
}
