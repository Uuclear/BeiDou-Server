package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.FileTreeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 脚本/配置文件树控制器，支持浏览文件树、读取与写入服务器脚本资源。
 * 委托 FileTreeService 在受控路径内操作游戏脚本与配置文件。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileTreeService fileTreeService;

    /**
     * 读取文件。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/read")
    public ResultBody<String> treeRead(@RequestBody SubmitBody<FileReadDTO> request) {
        return ResultBody.success(request, fileTreeService.readFile(request.getData().getCurrentKey(), request.getData().getTitle()));
    }

    /**
     * 写入文件。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "写入文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/write")
    public ResultBody<String> treeWrite(@RequestBody SubmitBody<FileWriteDTO> request) {
        fileTreeService.writeFile(request.getData().getCurrentKey(), request.getData().getTitle(), request.getData().getContent());
        return ResultBody.success(request,"写入成功");
    }

    /**
     * 读取文件树。
     *
     * @param request 请求体封装对象
     * @return 统一封装的 API 响应体
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件树")
    @PostMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<FileTreeNodeDTO>> tree(@RequestBody SubmitBody<FileTreeDTO> request) {
        return ResultBody.success(request, fileTreeService.tree(request.getData().getCurrentKey()));
    }

}
