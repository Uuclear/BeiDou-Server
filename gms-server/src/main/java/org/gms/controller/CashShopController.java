package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.ModifiedCashItemDO;
import org.gms.model.dto.CashShopBatchOnSaleReqDTO;
import org.gms.model.dto.CashShopSearchRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.pojo.CashCategory;
import org.gms.service.CashShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点券商城（Cash Shop）管理控制器，负责商城分类、商品查询及上下架操作。
 * 区别于 NPC 商店（ShopController），管理 modified_cash_item 等点券商品数据。
 */
@RestController
@RequestMapping("/cashShop")
@AllArgsConstructor
public class CashShopController {
    private final CashShopService cashShopService;

    /**
     * 获取商城全部分类。
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "获取商城全部分类")
    @GetMapping("/" + ApiConstant.LATEST + "/getAllCategoryList")
    public ResultBody<List<CashCategory>> getAllCategoryList() {
        return ResultBody.success(cashShopService.getAllCategoryList());
    }

    /**
     * 分页分类查询商品列表。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "分页分类查询商品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getCommodityByCategory")
    public ResultBody<Page<CashShopSearchRtnDTO>> getCommodityByCategory(@RequestBody SubmitBody<CashCategory> request) {
        return ResultBody.success(cashShopService.getCommodityByCategory(request.getData()));
    }

    /**
     * 根据sn查询商品明细。
     *
     * @param sn sn
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "根据sn查询商品明细")
    @GetMapping("/" + ApiConstant.LATEST + "/getCommodityBySn/{sn}")
    public ResultBody<CashShopSearchRtnDTO> getCommodityBySn(@PathVariable("sn") Integer sn) {
        return ResultBody.success(cashShopService.getCommodityBySn(sn));
    }

    /**
     * 上架商品。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "上架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/onSale")
    public ResultBody<Object> onSale(@RequestBody SubmitBody<ModifiedCashItemDO> request) {
        request.getData().setOnSale(1);
        cashShopService.changeOnSale(request.getData());
        return ResultBody.success();
    }

    /**
     * 下架商品。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "下架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/offSale")
    public ResultBody<Object> offSale(@RequestBody SubmitBody<ModifiedCashItemDO> request) {
        request.getData().setOnSale(0);
        cashShopService.changeOnSale(request.getData());
        return ResultBody.success();
    }

    /**
     * 批量上架商品。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "批量上架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/batchOnSale")
    public ResultBody<Object> batchOnSale(@RequestBody SubmitBody<CashShopBatchOnSaleReqDTO> request) {
        cashShopService.batchChangeOnSale(request.getData());
        return ResultBody.success();
    }
}
