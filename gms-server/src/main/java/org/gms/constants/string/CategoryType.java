package org.gms.constants.string;

import lombok.Getter;
import org.gms.util.I18nUtil;

/**
 * 道具分类类型枚举
 * <p>
 * 定义商城道具的分类类型，包括主页、活动、装备、消耗、设置、其他、宠物、礼包等分类。
 * 支持国际化，根据ID获取对应的分类类型和名称。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@Getter
public enum CategoryType {
    /** 主页 */
    MAIN(8, I18nUtil.getMessage("CategoryType.MAIN")),
    /** 活动 */
    EVENT(1, I18nUtil.getMessage("CategoryType.EVENT")),
    /** 装备 */
    EQUIP(2, I18nUtil.getMessage("CategoryType.EQUIP")),
    /** 消耗 */
    USE(3, I18nUtil.getMessage("CategoryType.USE")),
    /** 设置 */
    SET(4, I18nUtil.getMessage("CategoryType.SET")),
    /** 其他 */
    ETC(5, I18nUtil.getMessage("CategoryType.ETC")),
    /** 宠物 */
    PET(6, I18nUtil.getMessage("CategoryType.PET")),
    /** 礼包 */
    PACKAGE(7, I18nUtil.getMessage("CategoryType.PACKAGE")),
    ;

    /** 分类ID */
    private final int id;
    /** 分类名称（支持国际化） */
    private final String name;

    CategoryType(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 根据ID获取分类类型
     *
     * @param id 分类ID
     * @return 对应的分类类型枚举，未找到返回null
     */
    public static CategoryType ofId(int id) {
        for (CategoryType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据ID获取分类名称
     *
     * @param id 分类ID
     * @return 分类名称，未找到返回空字符串
     */
    public static String toName(int id) {
        CategoryType categoryType = ofId(id);
        return categoryType == null ? "" : categoryType.getName();
    }
}
