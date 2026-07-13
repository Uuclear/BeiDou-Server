package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 快速栏快捷键实体类，对应数据库表 quickslotkeymapped。
 * 存储快速栏快捷键映射。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("quickslotkeymapped")
public class QuickslotkeymappedDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 账号ID
     */
    private Integer accountid;

    /**
     * keymap
     */
    private Long keymap;

}
