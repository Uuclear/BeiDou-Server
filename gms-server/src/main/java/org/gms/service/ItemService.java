package org.gms.service;


import lombok.extern.slf4j.Slf4j;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.constants.inventory.ItemConstants;
import org.gms.exception.BizException;
import org.gms.server.ItemInformationProvider;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

/**
 * 道具业务服务，提供道具信息查询及道具相关配置维护。
 */
@Service
@Slf4j
public class ItemService {
    /**
     * 执行 getEquipmentInfoByItemId 相关业务逻辑。
     *
     * @param itemId itemId
     * @return Equip 类型结果
     */
    public Equip getEquipmentInfoByItemId(Integer itemId) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        String itemName = ii.getName(itemId);
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        if (!ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }

        return (Equip) ItemInformationProvider.getInstance().getEquipById(itemId);
    }
}
