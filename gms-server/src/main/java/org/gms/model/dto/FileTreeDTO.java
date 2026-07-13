package org.gms.model.dto;

import lombok.Data;

/**
 * 文件树请求DTO
 * 用于获取服务器文件目录树的请求参数
 */
@Data
public class FileTreeDTO {
    /**
     * 文件标题/名称
     */
    private String title;

    /**
     * 当前文件路径/键值
     */
    private String currentKey;
}
