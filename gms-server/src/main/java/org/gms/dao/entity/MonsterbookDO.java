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
 * 数据库表 `monsterbook` 的实体类（DO）。
 * <p>
 * 怪物收藏册进度表，记录角色对各怪物卡片的收集等级与数量。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("monsterbook")
public class MonsterbookDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer charid;

    @Id
    private Integer cardid;

    private Integer level;

}
