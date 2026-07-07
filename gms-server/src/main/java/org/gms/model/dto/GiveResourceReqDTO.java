package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * GM 发放资源请求 DTO，用于向指定世界/角色发放道具或装备，可附带属性与过期时间。
 */
@Setter
@Getter
public class GiveResourceReqDTO {
    private Integer worldId;
    private Integer playerId;
    private String player;
    private Byte type;
    private Integer id;
    private Integer quantity;
    private Float rate;
    private Short str;
    private Short dex;
    @JsonProperty("int")
    private Short _int;
    private Short luk;
    private Short hp;
    private Short mp;
    @JsonProperty("pAtk")
    private Short pAtk;
    @JsonProperty("mAtk")
    private Short mAtk;
    @JsonProperty("pDef")
    private Short pDef;
    @JsonProperty("mDef")
    private Short mDef;
    private Short acc;
    private Short avoid;
    private Short hands;
    private Short speed;
    private Short jump;
    private Byte upgradeSlot;
    private Long expire;
}
