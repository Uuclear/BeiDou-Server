package org.gms.model.dto;

import lombok.Data;

/**
 * 文件写入请求DTO
 * 用于向服务器文件写入内容的请求参数
 */
@Data
public class FileWriteDTO {
    /**
     * 文件标题/名称
     */
    private String title;

    /**
     * 当前文件路径/键值
     */
    private String currentKey;

    /**
     * 要写入的文件内容
     */
    private String content;
}
