package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * NX兑换码物品实体类，对应数据库表 nxcode_items。
 * 存储兑换码可兑换物品。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("nxcode_items")
public class NxcodeItemsDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    /**
     * 唯一ID
     */
    private Integer id;

    /**
     * codeid
     */
    private Integer codeid;

    /**
     * 类型
     */
    private Integer type;

    /**
     * item
     */
    private Integer item;

    /**
     * quantity
     */
    private Integer quantity;

}
