package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商城商品搜索结果返回DTO
 * 用于返回商城商品搜索结果，包含商品信息和默认值对比
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CashShopSearchRtnDTO {
    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 子分类ID
     */
    private Integer subcategoryId;

    /**
     * 子分类名称
     */
    private String subcategoryName;

    /**
     * 商品序列号SN
     */
    private Integer sn;

    /**
     * 物品ID
     */
    private Integer itemId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 默认价格
     */
    private Integer defaultPrice;

    /**
     * 有效期（毫秒）
     */
    private Long period;

    /**
     * 默认有效期
     */
    private Long defaultPeriod;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 默认优先级
     */
    private Integer defaultPriority;

    /**
     * 数量
     */
    private Short count;

    /**
     * 默认数量
     */
    private Short defaultCount;

    /**
     * 是否在售
     */
    private Integer onSale;

    /**
     * 默认是否在售
     */
    private Integer defaultOnSale;

    /**
     * 奖励
     */
    private Integer bonus;

    /**
     * 默认奖励
     */
    private Integer defaultBonus;

    /**
     * 抵用券价格
     */
    private Integer maplePoint;

    /**
     * 默认抵用券价格
     */
    private Integer defaultMaplePoint;

    /**
     * 金币价格
     */
    private Integer meso;

    /**
     * 默认金币价格
     */
    private Integer defaultMeso;

    /**
     * 是否仅限高级用户
     */
    private Integer forPremiumUser;

    /**
     * 默认是否仅限高级用户
     */
    private Integer defaultForPremiumUser;

    /**
     * 性别限制
     */
    private Integer gender;

    /**
     * 默认性别限制
     */
    private Integer defaultGender;

    /**
     * 分类
     */
    private Integer clz;

    /**
     * 默认分类
     */
    private Integer defaultClz;

    /**
     * 限购数量
     */
    private Integer limit;

    /**
     * 默认限购数量
     */
    private Integer defaultLimit;

    /**
     * 点卡购买标记
     */
    private Integer pbCash;

    /**
     * 默认点卡购买标记
     */
    private Integer defaultPBCash;

    /**
     * 抵用券购买标记
     */
    private Integer pbPoint;

    /**
     * 默认抵用券购买标记
     */
    private Integer defaultPBPoint;

    /**
     * 礼物赠送标记
     */
    private Integer pbGift;

    /**
     * 默认礼物赠送标记
     */
    private Integer defaultPBGift;

    /**
     * 礼包SN
     */
    private Integer packageSn;

    /**
     * 默认礼包SN
     */
    private Integer defaultPackageSn;
}
