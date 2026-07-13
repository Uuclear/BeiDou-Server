package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.ShopService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 游戏内商店管理控制器
 * 提供游戏NPC商店相关的Web API接口，包括商店查询、商品CRUD等功能
 * 注意：商城（CashShop）相关功能请使用CashShopController
 */
@RestController
@AllArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    /**
     * 商店服务，处理游戏商店和商品的业务逻辑
     */
    private final ShopService shopService;

    /**
     * 分页查询商店列表
     * @param request 包含分页参数和查询条件的请求体
     * @return 分页商店结果列表
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取商店列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getShopList")
    public ResultBody<Page<ShopSearchRtnDTO>> getShopList(@RequestBody SubmitBody<ShopSearchReqDTO> request) {
        return ResultBody.success(request, shopService.getShopList(request.getData()));
    }

    /**
     * 根据商店ID分页获取该商店的商品列表
     * @param request 包含商店ID和分页参数的请求体
     * @return 分页商品结果列表
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据商店id分页获取商品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getShopItemList")
    public ResultBody<Page<ShopItemSearchRtnDTO>> getShopItemList(@RequestBody SubmitBody<ShopSearchReqDTO> request) {
        return ResultBody.success(request, shopService.getShopItemList(request.getData()));
    }

    /**
     * 根据商品ID查询商品详细信息
     * @param id 商品ID
     * @return 商品详细信息DTO
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id查询商品信息")
    @GetMapping("/" + ApiConstant.LATEST + "/getShopItem/{id}")
    public ResultBody<ShopItemSearchRtnDTO> getShopItem(@PathVariable("id") Long id) {
        return ResultBody.success(shopService.getShopItem(id));
    }

    /**
     * 新增商店商品
     * @param request 包含新商品信息的请求体
     * @return 新增的商品ID
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "新增商品信息，返回新增的商品id")
    @PutMapping("/" + ApiConstant.LATEST + "/addShopItem")
    public ResultBody<Long> addShopItem(@RequestBody SubmitBody<ShopItemSearchRtnDTO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, shopService.modifyShopItem(request.getData(), false));
    }

    /**
     * 根据ID更新商店商品信息
     * @param request 包含更新商品信息的请求体，必须包含id字段
     * @return 操作成功结果
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id更新商品信息")
    @PostMapping("/" + ApiConstant.LATEST + "/updateShopItem")
    public ResultBody<Object> updateShopItem(@RequestBody SubmitBody<ShopItemSearchRtnDTO> request) {
        RequireUtil.requireNotNull(request.getData().getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        shopService.modifyShopItem(request.getData(), false);
        return ResultBody.success(request, null);
    }

    /**
     * 根据ID删除商店商品
     * @param id 要删除的商品ID
     * @return 操作成功结果
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id删除商品信息")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteShopItem/{id}")
    public ResultBody<Object> deleteShopItem(@PathVariable("id") Long id) {
        shopService.modifyShopItem(ShopItemSearchRtnDTO.builder().id(id).build(), true);
        return ResultBody.success(null);
    }
}
