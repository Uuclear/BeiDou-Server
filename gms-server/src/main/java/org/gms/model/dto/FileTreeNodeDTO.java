package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 文件树节点 DTO，表示配置目录中的单个文件或文件夹节点及其子节点。
 */
@Data
public class FileTreeNodeDTO {
    private String title;
    private String key;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileTreeNodeDTO> children;
    @JsonProperty("isLeaf")
    private boolean leaf;

    public FileTreeNodeDTO(File file, String key) {
        this.title = file.getName();
        this.key = key;
        this.children = file.isDirectory() ? Collections.emptyList() : null;
        this.leaf = !file.isDirectory();
    }
}
