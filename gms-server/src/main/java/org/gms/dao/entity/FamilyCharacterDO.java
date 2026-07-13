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
 * 家族成员实体类，对应数据库表 family_character。
 * 存储家族成员信息。
 *
 * @author sleep
 * @since 2024-05-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("family_character")
public class FamilyCharacterDO implements Serializable {

    @Serial
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * cid
     */
    private Integer cid;

    /**
     * familyid
     */
    private Integer familyid;

    /**
     * seniorid
     */
    private Integer seniorid;

    /**
     * reputation
     */
    private Integer reputation;

    /**
     * todaysrep
     */
    private Integer todaysrep;

    /**
     * totalreputation
     */
    private Integer totalreputation;

    /**
     * reptosenior
     */
    private Integer reptosenior;

    /**
     * precepts
     */
    private String precepts;

    /**
     * lastresettime
     */
    private Long lastresettime;

}
