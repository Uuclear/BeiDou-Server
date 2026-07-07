package org.gms.model.dto;

import lombok.Data;

/**
 * 服务端文件读取请求 DTO，指定要读取的配置文件标题 title 与当前节点键 currentKey。
 */
@Data
public class FileReadDTO {
    private String title;
    private String currentKey;
}
