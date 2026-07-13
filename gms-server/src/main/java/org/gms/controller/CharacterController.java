package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.model.dto.CharacterListItemDTO;
import org.gms.model.dto.ChrOnlineListReqDTO;
import org.gms.model.dto.ChrOnlineListRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.CharacterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 * 提供游戏角色相关的Web API接口，包括在线玩家查询、角色倍率调整、角色删除等功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/character")
public class CharacterController {
    /**
     * 角色服务，处理角色业务逻辑
     */
    private final CharacterService characterService;

    /**
     * 调整玩家个人倍率
     * @param submitBody 包含角色ID、倍率类型和倍率值的请求体
     *                   extendName可选值：expRate（经验倍率）| mesoRate（金币倍率）| dropRate（掉落倍率）
     * @return 操作成功结果
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "调整玩家个人倍率，extendName为：expRate | mesoRate | dropRate")
    @PostMapping("/" + ApiConstant.LATEST + "/updateRate")
    public ResultBody<Object> updateRate(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.updateRate(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 重置玩家单个个人倍率为默认值
     * @param submitBody 包含角色ID和倍率类型的请求体
     *                   extendName可选值：expRate | mesoRate | dropRate
     * @return 操作成功结果
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "重置玩家个人倍率，extendName为：expRate | mesoRate | dropRate")
    @PostMapping("/" + ApiConstant.LATEST + "/resetRate")
    public ResultBody<Object> resetRate(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.resetRate(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 重置玩家所有个人倍率为默认值
     * @param submitBody 包含角色ID的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "重置玩家个人所有倍率")
    @GetMapping("/" + ApiConstant.LATEST + "/resetRates")
    public ResultBody<Object> resetRates(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.resetRates(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 分页查询在线玩家列表
     * @param submitBody 包含分页参数和查询条件的请求体
     * @return 分页在线玩家列表
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "查询在线玩家列表")
    @PostMapping("/" + ApiConstant.LATEST + "/online/list")
    public ResultBody<Page<ChrOnlineListRtnDTO>> onlineList(@RequestBody SubmitBody<ChrOnlineListReqDTO> submitBody) {
        return ResultBody.success(characterService.getChrOnlineList(submitBody.getData()));
    }

    /**
     * 获取指定账号下的所有角色列表
     * @param accountId 账号ID
     * @return 该账号下的角色列表
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "获取账号下角色列表")
    @GetMapping("/" + ApiConstant.LATEST + "/account/{accountId}")
    public ResultBody<List<CharacterListItemDTO>> getAccountCharacters(@PathVariable("accountId") int accountId) {
        return ResultBody.success(characterService.getCharacterListByAccountId(accountId));
    }

    /**
     * 删除指定角色
     * 需要先检查角色是否在线，在线角色不允许删除
     * @param cid 角色ID
     * @return 操作成功结果
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "删除角色")
    @DeleteMapping("/" + ApiConstant.LATEST + "/{cid}")
    public ResultBody<Object> delete(@PathVariable("cid") int cid) {
        characterService.deleteCharacterWithOnlineCheck(cid);
        return ResultBody.success();
    }
}
