package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.AccountsDO;
import org.gms.model.dto.*;
import org.gms.service.AccountService;
import org.gms.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 账号管理控制器
 * 提供账号相关的Web API接口，包括账号查询、注册、资料修改、封禁/解封、删除等功能
 */
@RestController
@RequestMapping("/account")
public class AccountController {
    /**
     * 账号服务，处理账号业务逻辑
     */
    private final AccountService accountService;

    /**
     * 角色服务，处理角色相关业务逻辑（用于账号删除时级联删除角色）
     */
    private final CharacterService characterService;

    /**
     * 构造函数，通过依赖注入注入所需服务
     * @param accountService 账号服务
     * @param characterService 角色服务
     */
    @Autowired
    public AccountController(AccountService accountService, CharacterService characterService) {
        this.accountService = accountService;
        this.characterService = characterService;
    }

    /**
     * 获取当前登录用户信息
     * @return 当前登录用户的账号信息
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "获取我的信息")
    @GetMapping("/" + ApiConstant.LATEST + "/info")
    public ResultBody<AccountsDO> info() {
        return ResultBody.success(accountService.getCurrentUser());
    }

    /**
     * 分页查询账号列表
     * 支持按账号ID、名称、登录时间、创建时间等条件过滤
     * @param page 页码，从1开始
     * @param size 每页条数
     * @param id 账号ID（精确匹配）
     * @param name 账号名称（模糊匹配）
     * @param lastLoginStart 最后登录开始时间
     * @param lastLoginEnd 最后登录结束时间
     * @param createdAtStart 创建时间开始
     * @param createdAtEnd 创建时间结束
     * @return 分页账号列表
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "获取账号列表")
    @GetMapping("/" + ApiConstant.LATEST)
    public ResultBody<Page<AccountsDO>> getAccountList(@RequestParam(name = "page", required = false) Integer page,
                                                       @RequestParam(name = "size", required = false) Integer size,
                                                       @RequestParam(name = "id", required = false) Integer id,
                                                       @RequestParam(name = "name", required = false) String name,
                                                       @RequestParam(name = "lastLoginStart", required = false) String lastLoginStart,
                                                       @RequestParam(name = "lastLoginEnd", required = false) String lastLoginEnd,
                                                       @RequestParam(name = "createdAtStart", required = false) String createdAtStart,
                                                       @RequestParam(name = "createdAtEnd", required = false) String createdAtEnd) {
        return ResultBody.success(accountService.getAccountList(page, size, id, name, lastLoginStart, lastLoginEnd, createdAtStart, createdAtEnd));
    }

    /**
     * 注册新账号
     * @param submitBody 包含新账号注册信息的请求体
     * @return 操作成功结果
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "注册账号")
    @PostMapping("/" + ApiConstant.LATEST)
    public ResultBody<Object> register(@RequestBody SubmitBody<AddAccountDTO> submitBody) throws NoSuchAlgorithmException {
        accountService.addAccount(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 用户更新自己的账号资料
     * 需要验证旧密码，新密码留空则不修改密码
     * @param submitBody 包含用户更新信息的请求体
     * @return 操作成功结果
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "更新账号资料[用户](须校验旧密码,新密码留空则不修改)")
    @PutMapping("/" + ApiConstant.LATEST)
    public ResultBody<Object> updateByUser(@RequestBody SubmitBody<UpdateAccountByUserDTO> submitBody) throws NoSuchAlgorithmException {
        accountService.updateAccountByUser(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * GM更新指定账号的资料
     * 可以修改所有账号字段，包括点券、权限等级等，需要账号离线
     * @param id 要修改的账号ID
     * @param submitBody 包含GM更新信息的请求体
     * @return 操作成功结果
     * @throws NoSuchAlgorithmException 密码加密算法不存在时抛出
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "更新账号资料[GM]")
    @PutMapping("/" + ApiConstant.LATEST + "/{id}")
    public ResultBody<Object> updateByGm(@PathVariable("id") int id,
                                         @RequestBody SubmitBody<UpdateAccountByGmDTO> submitBody) throws NoSuchAlgorithmException {
        accountService.updateAccountByGM(id, submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 删除指定账号及其所有关联数据
     * 会级联删除该账号下的所有角色
     * @param id 要删除的账号ID
     * @return 操作成功结果
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "删除账号")
    @DeleteMapping("/" + ApiConstant.LATEST + "/{id}")
    public ResultBody<Object> delete(@PathVariable("id") int id) {
        characterService.deleteAccount(id);
        return ResultBody.success();
    }

    /**
     * 重置指定账号的登录状态为未登录
     * 用于处理账号异常卡在登录状态的情况
     * @param id 要重置的账号ID
     * @return 操作成功结果
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "重置在线状态")
    @PutMapping("/" + ApiConstant.LATEST + "/{id}/reset/logged")
    public ResultBody<Object> resetLoggedIn(@PathVariable("id") int id) {
        accountService.resetAllLoggedIn(id);
        return ResultBody.success();
    }

    /**
     * 封停指定账号
     * 同时封禁在线角色的MAC、IP，并强制下线
     * @param id 要封停的账号ID
     * @param submitBody 包含封禁原因的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "封停账号")
    @PutMapping("/" + ApiConstant.LATEST + "/{id}/ban")
    public ResultBody<Object> banAccount(@PathVariable("id") int id,
                                         @RequestBody SubmitBody<Map<String, String>> submitBody) {
        accountService.banAccount(id, submitBody.getData().get("reason"));
        return ResultBody.success();
    }

    /**
     * 解封指定账号
     * 同时解封对应的MAC和IP封禁记录
     * @param id 要解封的账号ID
     * @return 操作成功结果
     */
    @Tag(name = "/account/" + ApiConstant.LATEST)
    @Operation(summary = "解封账号")
    @PutMapping("/" + ApiConstant.LATEST + "/{id}/unban")
    public ResultBody<Object> unbanAccount(@PathVariable("id") int id) {
        accountService.unbanAccount(id);
        return ResultBody.success();
    }
}
