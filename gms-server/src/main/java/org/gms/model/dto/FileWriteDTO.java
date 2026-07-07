package org.gms.model.dto;

import lombok.Data;

/**
 * 服务端文件写入请求 DTO，携带目标文件标识与待写入的文本内容。
 */
@Data
public class FileWriteDTO {
    private String title;
    private String currentKey;
    private String content;
}
