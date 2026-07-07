package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.model.dto.ChrOnlineListReqDTO;
import org.gms.model.dto.ChrOnlineListRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.CharacterService;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器，提供在线玩家查询及个人经验/金币/掉落倍率调整接口。
 * 委托 CharacterService 操作游戏内存中的角色与扩展属性数据。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/character")
public class CharacterController {
    private final CharacterService characterService;

    /**
     * 调整玩家个人倍率，extendName为：expRate | mesoRate | dropRate。
     *
     * @param submitBody 提交数据封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "调整玩家个人倍率，extendName为：expRate | mesoRate | dropRate")
    @PostMapping("/" + ApiConstant.LATEST + "/updateRate")
    public ResultBody<Object> updateRate(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.updateRate(submitBody.getData());
        return ResultBody.success();
    }


    /**
     * 重置玩家个人倍率，extendName为：expRate | mesoRate | dropRate。
     *
     * @param submitBody 提交数据封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "重置玩家个人倍率，extendName为：expRate | mesoRate | dropRate")
    @PostMapping("/" + ApiConstant.LATEST + "/resetRate")
    public ResultBody<Object> resetRate(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.resetRate(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 重置玩家个人所有倍率。
     *
     * @param submitBody 提交数据封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "重置玩家个人所有倍率")
    @GetMapping("/" + ApiConstant.LATEST + "/resetRates")
    public ResultBody<Object> resetRates(@RequestBody SubmitBody<ExtendValueDO> submitBody) {
        characterService.resetRates(submitBody.getData());
        return ResultBody.success();
    }

    /**
     * 查询在线玩家列表。
     *
     * @param submitBody 提交数据封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/character/" + ApiConstant.LATEST)
    @Operation(summary = "查询在线玩家列表")
    @PostMapping("/" + ApiConstant.LATEST + "/online/list")
    public ResultBody<Page<ChrOnlineListRtnDTO>> onlineList(@RequestBody SubmitBody<ChrOnlineListReqDTO> submitBody) {
        return ResultBody.success(characterService.getChrOnlineList(submitBody.getData()));
    }
}
