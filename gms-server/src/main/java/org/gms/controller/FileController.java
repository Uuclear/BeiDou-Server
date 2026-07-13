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
 * 文件管理控制器
 * 提供服务器文件浏览和编辑的Web API接口，支持安全的文件树浏览、文件读取和写入功能
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
    /**
     * 文件树服务，处理文件浏览、读取和写入逻辑，包含路径安全校验
     */
    private final FileTreeService fileTreeService;

    /**
     * 读取文件内容
     * @param request 包含文件树键和文件标题的请求体
     * @return 文件内容字符串
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/read")
    public ResultBody<String> treeRead(@RequestBody SubmitBody<FileReadDTO> request) {
        return ResultBody.success(request, fileTreeService.readFile(request.getData().getCurrentKey(), request.getData().getTitle()));
    }

    /**
     * 写入文件内容
     * @param request 包含文件树键、文件标题和写入内容的请求体
     * @return 写入成功提示
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "写入文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/write")
    public ResultBody<String> treeWrite(@RequestBody SubmitBody<FileWriteDTO> request) {
        fileTreeService.writeFile(request.getData().getCurrentKey(), request.getData().getTitle(), request.getData().getContent());
        return ResultBody.success(request,"写入成功");
    }

    /**
     * 读取文件树结构
     * @param request 包含当前文件树键的请求体
     * @return 文件树节点列表
     */
    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件树")
    @PostMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<FileTreeNodeDTO>> tree(@RequestBody SubmitBody<FileTreeDTO> request) {
        return ResultBody.success(request, fileTreeService.tree(request.getData().getCurrentKey()));
    }

}
