package org.gms.model.dto;

import lombok.Data;

/**
 * 文件读取请求DTO
 * 用于读取服务器文件内容的请求参数
 */
@Data
public class FileReadDTO {
    /**
     * 文件标题/名称
     */
    private String title;

    /**
     * 当前文件路径/键值
     */
    private String currentKey;
}
