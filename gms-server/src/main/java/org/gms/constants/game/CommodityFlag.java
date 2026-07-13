package org.gms.constants.game;

import lombok.Getter;
import org.gms.client.inventory.Item;
import org.gms.net.packet.OutPacket;
import org.gms.server.CashShop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 商城商品标志位枚举
 * <p>
 * 定义现金商城商品的各种属性标志位，用于序列化商品信息到数据包。
 * 每个枚举值对应一个属性字段，包含标志位值、排序序号、描述和数据包写入函数。
 * </p>
 * <p>
 * 字段名采用英文无需多语言，desc字段仅作为中文参考描述。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Getter
public enum CommodityFlag {

    /**
     * 商品序列号（固有部分）
     */
    SN(0, 0, "SN", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 商品标志（固有部分）
     */
    FLAG(0, 1, "FLAG", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 物品ID（自定义部分）
     */
    ITEM_ID(1, 2, "物品ID", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 数量
     */
    COUNT(1 << 1, 3, "数量", (p, n) -> p.writeShort(n.intValue())),

    /**
     * 价格
     */
    PRICE(1 << 2, 5, "价格", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 属性奖励
     */
    BONUS(1 << 3, 6, "属性奖励", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 优先级
     */
    PRIORITY(1 << 4, 4, "优先级", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 有效期
     */
    PERIOD(1 << 5, 7, "有效期", (p, n) -> p.writeShort(n.intValue())),

    /**
     * 抵用券
     */
    MAPLE_POINT(1 << 6, 8, "抵用券", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 金币
     */
    MESO(1 << 7, 9, "金币", (p, n) -> p.writeInt(n.intValue())),

    /**
     * 高级用户专属
     */
    FOR_PREMIUM_USER(1 << 8, 10, "高级用户", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 商品性别限制
     */
    COMMODITY_GENDER(1 << 9, 11, "性别", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 是否在售
     */
    ON_SALE(1 << 10, 12, "是否销售", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 商品标签/分类
     */
    CLASS(1 << 11, 13, "标签", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 限时特卖
     */
    LIMIT(1 << 12, 14, "限时特卖", (p, n) -> p.writeByte(n.intValue())),

    /**
     * 未知字段PB_CASH
     */
    PB_CASH(1 << 13, 15, "Unknown", (p, n) -> p.writeShort(n.intValue())),

    /**
     * 未知字段PB_POINT
     */
    PB_POINT(1 << 14, 16, "Unknown", (p, n) -> p.writeShort(n.intValue())),

    /**
     * 未知字段PB_GIFT
     */
    PB_GIFT(1 << 15, 17, "Unknown", (p, n) -> p.writeShort(n.intValue())),

    /**
     * 礼包SN
     */
    PACKAGE_SN(1 << 16, 18, "礼包SN", (p, n) -> {
        List<Item> itemList = CashShop.CashItemFactory.getPackage(n.intValue());
        if (itemList.isEmpty()) {
            p.writeByte(0);
        } else {
            p.writeByte(itemList.size());
            itemList.forEach(item -> p.writeInt(item.getSN()));
        }
    }),

    // 以下标志位在v83版本中不支持
    REQ_POP(1 << 17, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    REQ_LEVEL(1 << 18, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    TERM_START(1 << 19, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    TERM_END(1 << 20, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    REFUNDABLE(1 << 21, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    BOMB_SALE(1 << 22, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    FORCED_CATEGORY(1 << 23, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    GAME_WORLD(1 << 24, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    TOKEN(1 << 25, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    LIMIT_MAX(1 << 26, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    LIMIT_QUEST_ID(1 << 27, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    ORIGINAL_PRICE(1 << 28, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    DISCOUNT(1 << 29, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    DISCOUNT_RATE(1 << 30, -1, "Unknown83", (p, n) -> p.writeByte(0)),
    MILEAGE_RATE(1L << 31, -1, "Unknown83", (p, n) -> p.writeByte(0)),

    /**
     * 所有标志位（特殊值）
     */
    ALL(-1, -1, "Unknown83", (p, n) -> p.writeByte(0));

    /**
     * 标志位值（位掩码）
     */
    private final long flag;

    /**
     * 排序序号
     */
    private final int sort;

    /**
     * 中文描述
     */
    private final String desc;

    /**
     * 数据包写入函数，用于将该字段值写入OutPacket
     */
    private final BiConsumer<OutPacket, Number> writeMapper;

    /**
     * 构造函数
     *
     * @param flag        标志位值
     * @param sort        排序序号
     * @param desc        描述
     * @param writeMapper 数据包写入函数
     */
    CommodityFlag(int flag, int sort, String desc, BiConsumer<OutPacket, Number> writeMapper) {
        this.flag = flag;
        this.sort = sort;
        this.desc = desc;
        this.writeMapper = writeMapper;
    }

    /**
     * 获取所有可用的（v83版本支持的）标志位，按排序序号排序
     *
     * @return 排序后的可用CommodityFlag列表
     */
    public static List<CommodityFlag> getAvailableSortedValues() {
        List<CommodityFlag> result = new ArrayList<>();
        for (CommodityFlag value : values()) {
            if (value.sort == -1 || "Unknown83".equals(value.desc)) {
                continue;
            }
            result.add(value);
        }
        result.sort(Comparator.comparing(CommodityFlag::getSort));
        return result;
    }
}
