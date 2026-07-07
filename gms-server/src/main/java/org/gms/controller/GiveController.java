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
 * 资源发放控制器，向指定玩家分发道具、金币等游戏资源。
 * 封装 GM 发放操作，由 GiveService 对接游戏服。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/give")
public class GiveController {
    @Autowired
    private final GiveService giveService;

    /**
     * 给玩家分发资源。
     *
     * @param submitBody 提交数据封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/give/" + ApiConstant.LATEST)
    @Operation(summary = "给玩家分发资源")
    @PostMapping("/" + ApiConstant.LATEST + "/resource")
    public ResultBody<Object> giveResource(@RequestBody SubmitBody<GiveResourceReqDTO> submitBody) {
        giveService.give(submitBody.getData());
        return ResultBody.success();
    }
}
