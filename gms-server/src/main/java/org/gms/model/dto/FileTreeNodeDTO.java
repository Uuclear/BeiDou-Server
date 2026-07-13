package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 文件树节点DTO
 * 用于表示文件目录树中的单个节点（文件或文件夹）
 */
@Data
public class FileTreeNodeDTO {
    /**
     * 节点显示名称
     */
    private String title;

    /**
     * 节点唯一键（路径）
     */
    private String key;

    /**
     * 子节点列表，为null时不序列化
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileTreeNodeDTO> children;

    /**
     * 是否为叶子节点（文件）
     */
    @JsonProperty("isLeaf")
    private boolean leaf;

    /**
     * 构造函数，根据File对象创建树节点
     * @param file 文件对象
     * @param key 节点键值（路径）
     */
    public FileTreeNodeDTO(File file, String key) {
        this.title = file.getName();
        this.key = key;
        this.children = file.isDirectory() ? Collections.emptyList() : null;
        this.leaf = !file.isDirectory();
    }
}
