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
 * NPC 游戏币商店管理控制器，维护商店及商品（非点券商城）的查询与 CRUD。
 * 点券商城相关接口见 CashShopController，本控制器委托 ShopService 操作 shops 数据表。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/shop")
public class ShopController {
    private final ShopService shopService;

    /**
     * 分页获取商店列表。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取商店列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getShopList")
    public ResultBody<Page<ShopSearchRtnDTO>> getShopList(@RequestBody SubmitBody<ShopSearchReqDTO> request) {
        return ResultBody.success(request, shopService.getShopList(request.getData()));
    }

    /**
     * 根据商店id分页获取商品列表。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据商店id分页获取商品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getShopItemList")
    public ResultBody<Page<ShopItemSearchRtnDTO>> getShopItemList(@RequestBody SubmitBody<ShopSearchReqDTO> request) {
        return ResultBody.success(request, shopService.getShopItemList(request.getData()));
    }

    /**
     * 根据id查询商品信息。
     *
     * @param id 记录主键 ID
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id查询商品信息")
    @GetMapping("/" + ApiConstant.LATEST + "/getShopItem/{id}")
    public ResultBody<ShopItemSearchRtnDTO> getShopItem(@PathVariable("id") Long id) {
        return ResultBody.success(shopService.getShopItem(id));
    }

    /**
     * 新增商品信息，返回新增的商品id。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "新增商品信息，返回新增的商品id")
    @PutMapping("/" + ApiConstant.LATEST + "/addShopItem")
    public ResultBody<Long> addShopItem(@RequestBody SubmitBody<ShopItemSearchRtnDTO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, shopService.modifyShopItem(request.getData(), false));
    }

    /**
     * 根据id更新商品信息。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
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
     * 根据id删除商品信息。
     *
     * @param id 记录主键 ID
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/shop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id删除商品信息")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteShopItem/{id}")
    public ResultBody<Object> deleteShopItem(@PathVariable("id") Long id) {
        shopService.modifyShopItem(ShopItemSearchRtnDTO.builder().id(id).build(), true);
        return ResultBody.success(null);
    }
}
