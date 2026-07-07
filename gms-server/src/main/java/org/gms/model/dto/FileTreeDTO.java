package org.gms.model.dto;

import lombok.Data;

/**
 * 服务端文件树响应 DTO，返回配置目录的树形结构根节点。
 */
@Data
public class FileTreeDTO {
    private String title;
    private String currentKey;
}
