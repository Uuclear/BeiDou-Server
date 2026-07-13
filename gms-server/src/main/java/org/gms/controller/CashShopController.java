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
 * 商城管理控制器
 * 提供游戏商城相关的Web API接口，包括商品分类查询、商品列表、商品上下架、批量修改等功能
 */
@RestController
@RequestMapping("/cashShop")
@AllArgsConstructor
public class CashShopController {
    /**
     * 商城服务，处理商城商品的查询和修改逻辑
     */
    private final CashShopService cashShopService;

    /**
     * 获取商城所有商品分类列表
     * 从WZ文件中读取商品分类信息
     * @return 商城分类列表
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "获取商城全部分类")
    @GetMapping("/" + ApiConstant.LATEST + "/getAllCategoryList")
    public ResultBody<List<CashCategory>> getAllCategoryList() {
        return ResultBody.success(cashShopService.getAllCategoryList());
    }

    /**
     * 根据分类分页查询商品列表
     * 支持按上架状态、物品ID过滤，固定每页10条
     * @param request 包含分类ID和子分类ID的请求体
     * @return 分页商品结果
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "分页分类查询商品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getCommodityByCategory")
    public ResultBody<Page<CashShopSearchRtnDTO>> getCommodityByCategory(@RequestBody SubmitBody<CashCategory> request) {
        return ResultBody.success(cashShopService.getCommodityByCategory(request.getData()));
    }

    /**
     * 根据商品SN获取商品详情
     * @param sn 商品序列号
     * @return 商品详情DTO
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "根据sn查询商品明细")
    @GetMapping("/" + ApiConstant.LATEST + "/getCommodityBySn/{sn}")
    public ResultBody<CashShopSearchRtnDTO> getCommodityBySn(@PathVariable("sn") Integer sn) {
        return ResultBody.success(cashShopService.getCommodityBySn(sn));
    }

    /**
     * 上架商品
     * 设置商品状态为上架（onSale=1），并更新商品信息
     * @param request 包含商品信息的请求体
     * @return 操作成功结果
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
     * 下架商品
     * 设置商品状态为下架（onSale=0）
     * @param request 包含商品SN的请求体
     * @return 操作成功结果
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
     * 批量上架商品并统一修改属性
     * 支持批量修改价格、数量、有效期
     * @param request 包含批量商品数据和修改类型/值的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "批量上架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/batchOnSale")
    public ResultBody<Object> batchOnSale(@RequestBody SubmitBody<CashShopBatchOnSaleReqDTO> request) {
        cashShopService.batchChangeOnSale(request.getData());
        return ResultBody.success();
    }
}
