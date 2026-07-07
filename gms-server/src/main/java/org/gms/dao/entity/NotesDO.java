package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据库表 `notes` 的实体类（DO）。
 * <p>
 * 角色备忘录/便签表，保存玩家在游戏内记录的文本便签。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notes")
public class NotesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String to;

    private String from;

    private String message;

    private Long timestamp;

    private Integer fame;

    private Integer deleted;

}
