package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 背包管理控制器
 * 提供玩家背包相关的Web API接口，包括背包分类查询、玩家物品查询、物品修改和删除等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {
    /**
     * 背包服务，处理玩家背包物品的查询和修改逻辑
     */
    private final InventoryService inventoryService;

    /**
     * 获取所有背包分类列表
     * @return 背包分类DTO列表
     */
    @Tag(name = "/inventory/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有背包分类")
    @GetMapping("/" + ApiConstant.LATEST + "/getInventoryTypeList")
    public ResultBody<List<InventoryTypeRtnDTO>> getInventoryTypeList() {
        return ResultBody.success(inventoryService.getInventoryTypeList());
    }

    /**
     * 根据条件分页查询有背包物品的玩家列表
     * @param request 包含分页参数和查询条件的请求体
     * @return 分页玩家列表
     */
    @Tag(name = "/inventory/" + ApiConstant.LATEST)
    @Operation(summary = "根据条件获取背包玩家列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getCharacterList")
    public ResultBody<Page<InventorySearchReqDTO>> getCharacterList(@RequestBody SubmitBody<InventorySearchReqDTO> request) {
        return ResultBody.success(inventoryService.getCharacterList(request.getData()));
    }

    /**
     * 获取指定玩家指定背包分类下的所有物品
     * @param request 包含玩家ID和背包类型的请求体
     * @return 物品列表
     */
    @Tag(name = "/inventory/" + ApiConstant.LATEST)
    @Operation(summary = "获取指定玩家背包分类下的所有物品")
    @PostMapping("/" + ApiConstant.LATEST + "/getInventoryList")
    public ResultBody<List<InventorySearchRtnDTO>> getInventoryList(@RequestBody SubmitBody<InventorySearchReqDTO> request) {
        return ResultBody.success(inventoryService.getInventoryList(request.getData()));
    }

    /**
     * 根据条件修改玩家背包物品
     * @param request 包含物品修改信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/inventory/" + ApiConstant.LATEST)
    @Operation(summary = "根据条件修改玩家背包")
    @PostMapping("/" + ApiConstant.LATEST + "/updateInventory")
    public ResultBody<Object> updateInventory(@RequestBody SubmitBody<InventorySearchRtnDTO> request) {
        inventoryService.updateInventory(request.getData());
        return ResultBody.success();
    }

    /**
     * 根据条件删除玩家背包物品
     * @param request 包含要删除物品信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/inventory/" + ApiConstant.LATEST)
    @Operation(summary = "根据条件删除玩家背包")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteInventory")
    public ResultBody<Object> deleteInventory(@RequestBody SubmitBody<InventorySearchRtnDTO> request) {
        inventoryService.deleteInventory(request.getData());
        return ResultBody.success();
    }
}
