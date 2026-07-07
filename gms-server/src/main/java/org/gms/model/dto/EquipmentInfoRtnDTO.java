package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 装备详情查询响应 DTO，返回装备的强化、四维、攻防等完整属性数值。
 */
@Setter
@Getter
public class EquipmentInfoRtnDTO {
//    private Integer worldId;
//    private Integer playerId;
//    private String player;
//    private Byte type;
//    private Integer id;
//    private Integer quantity;
//    private Integer rate;
    private Short str;
    private Short dex;
    @JsonProperty("int")
    private Short _int;
    private Short luk;
    private Short hp;
    private Short mp;
    private Short pAtk;
    private Short mAtk;
    private Short pDef;
    private Short mDef;
    private Short acc;
    private Short avoid;
    private Short hands;
    private Short speed;
    private Short jump;
    private Byte upgradeSlot;
    private Long expire;
}
