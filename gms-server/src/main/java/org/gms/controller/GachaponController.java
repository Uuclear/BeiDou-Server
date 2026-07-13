package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GachaponRewardDO;
import org.gms.dao.entity.GachaponRewardPoolDO;
import org.gms.model.dto.*;
import org.gms.service.GachaponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 百宝箱（抽奖）管理控制器
 * 提供百宝箱奖池和奖品管理的Web API接口，包括奖池CRUD、奖品CRUD等功能
 */
@RestController
@RequestMapping("/gachapon")
public class GachaponController {
    /**
     * 百宝箱服务，处理奖池和奖品的业务逻辑
     */
    @Autowired
    private GachaponService gachaponService;

    /**
     * 分页查询百宝箱奖池列表
     * @param request 包含分页参数和查询条件的请求体
     * @return 分页奖池结果列表
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "获取奖池列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getPools")
    public ResultBody<Page<GachaponPoolSearchRtnDTO>> getPools(@RequestBody SubmitBody<GachaponPoolSearchReqDTO> request) {
        return ResultBody.success(request, gachaponService.getPools(request.getData()));
    }

    /**
     * 创建或更新百宝箱奖池
     * @param request 包含奖池信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "创建或更新奖池")
    @PostMapping("/" + ApiConstant.LATEST + "/updatePool")
    public ResultBody<Object> updatePool(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        gachaponService.updatePool(request.getData());
        return ResultBody.success();
    }

    /**
     * 删除指定的百宝箱奖池
     * @param request 包含奖池ID的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖池")
    @PostMapping("/" + ApiConstant.LATEST + "/deletePool")
    public ResultBody<Object> deletePool(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        gachaponService.deletePool(request.getData().getId());
        return ResultBody.success();
    }

    /**
     * 获取指定奖池下的所有奖品列表
     * @param request 包含奖池ID的请求体
     * @return 奖品列表
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "获取奖品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getRewards")
    public ResultBody<List<GachaponRewardDO>> getRewards(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        return ResultBody.success(gachaponService.getRewards(request.getData().getId()));
    }

    /**
     * 创建或更新百宝箱奖品
     * @param request 包含奖品信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "创建或更新奖品")
    @PostMapping("/" + ApiConstant.LATEST + "/updateReward")
    public ResultBody<Object> updateReward(@RequestBody SubmitBody<GachaponRewardDO> request) {
        gachaponService.updateReward(request.getData());
        return ResultBody.success();
    }

    /**
     * 删除指定的百宝箱奖品
     * @param request 包含奖品ID的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖品")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteReward")
    public ResultBody<Object> deleteReward(@RequestBody SubmitBody<GachaponRewardDO> request) {
        gachaponService.deleteReward(request.getData().getId());
        return ResultBody.success();
    }
}
