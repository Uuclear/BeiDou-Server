package org.gms.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.GiveResourceReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.GiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物品发放控制器
 * 提供GM给玩家发放游戏资源的Web API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/give")
public class GiveController {
    /**
     * 发放服务，处理给玩家发放物品、点券、金币等资源的业务逻辑
     */
    @Autowired
    private final GiveService giveService;

    /**
     * 给指定玩家发放游戏资源
     * @param submitBody 包含目标玩家信息和要发放的资源信息的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/give/" + ApiConstant.LATEST)
    @Operation(summary = "给玩家分发资源")
    @PostMapping("/" + ApiConstant.LATEST + "/resource")
    public ResultBody<Object> giveResource(@RequestBody SubmitBody<GiveResourceReqDTO> submitBody) {
        giveService.give(submitBody.getData());
        return ResultBody.success();
    }
}
