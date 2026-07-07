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
 * 数据库表 `quickslotkeymapped` 的实体类（DO）。
 * <p>
 * 快捷栏按键映射表，持久化角色快捷栏槽位与键盘按键的绑定关系。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("quickslotkeymapped")
public class QuickslotkeymappedDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer accountid;

    private Long keymap;

}
