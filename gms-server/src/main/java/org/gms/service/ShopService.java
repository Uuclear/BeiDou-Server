package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.ShopitemsDO;
import org.gms.dao.mapper.ShopitemsMapper;
import org.gms.dao.mapper.ShopsMapper;
import org.gms.model.dto.ShopItemSearchRtnDTO;
import org.gms.model.dto.ShopSearchReqDTO;
import org.gms.model.dto.ShopSearchRtnDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.ShopFactory;
import org.gms.server.life.LifeFactory;
import org.gms.util.BasePageUtil;
import org.gms.util.Pair;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.gms.dao.entity.table.ShopitemsDOTableDef.SHOPITEMS_D_O;
import static org.gms.dao.entity.table.ShopsDOTableDef.SHOPS_D_O;

/**
 * NPC 商店业务服务，维护游戏币商店及商品配置的查询与 CRUD。
 */
@Service
@AllArgsConstructor
public class ShopService {
    private final ShopsMapper shopsMapper;
    private final ShopitemsMapper shopitemsMapper;

    /**
     * 执行 getShopList 相关业务逻辑。
     *
     * @param data 业务数据载荷
     * @return Page<ShopSearchRtnDTO> 类型结果
     */
    public Page<ShopSearchRtnDTO> getShopList(ShopSearchReqDTO data) {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(SHOPS_D_O)
                .leftJoin(SHOPITEMS_D_O).on(SHOPS_D_O.SHOPID.eq(SHOPITEMS_D_O.SHOPID));
        if (data.getNpcId() != null) {
            queryWrapper.and(SHOPS_D_O.NPCID.eq(data.getNpcId()));
        }
        if (data.getShopId() != null) {
            queryWrapper.and(SHOPS_D_O.SHOPID.eq(data.getShopId()));
        }
        if (data.getItemId() != null) {
            queryWrapper.and(SHOPITEMS_D_O.ITEMID.eq(data.getItemId()));
        }
        List<Row> queryAsList = shopsMapper.selectListByQueryAs(queryWrapper, Row.class);
        List<ShopSearchRtnDTO> matchedShopsDOList = new ArrayList<>();
        for (Row row : queryAsList) {
            Integer npcId = row.getInt("npcid");
            String npcName = LifeFactory.getNPCName(npcId);
            if (RequireUtil.isEmpty(npcName)) {
                continue;
            }
            if (!RequireUtil.isEmpty(data.getNpcName()) && !npcName.contains(data.getNpcName())) {
                continue;
            }
            Integer itemId = row.getInt("itemid");
            if (itemId != null) {
                if (data.getItemId() != null && !Objects.equals(itemId, data.getItemId())) {
                    continue;
                }
                String itemName = ItemInformationProvider.getInstance().getName(itemId);
                if (!RequireUtil.isEmpty(data.getItemName()) && !RequireUtil.isEmpty(itemName) && !itemName.contains(data.getItemName())) {
                    continue;
                }
            }
            matchedShopsDOList.add(ShopSearchRtnDTO.builder()
                    .shopId(row.getLong("shopid"))
                    .npcId(row.getInt("npcid"))
                    .npcName(npcName)
                    .build());
        }
        return BasePageUtil.create(matchedShopsDOList.stream().distinct().toList(), data).page();
    }

    /**
     * 执行 getShopItemList 相关业务逻辑。
     *
     * @param data 业务数据载荷
     * @return Page<ShopItemSearchRtnDTO> 类型结果
     */
    public Page<ShopItemSearchRtnDTO> getShopItemList(ShopSearchReqDTO data) {
        QueryWrapper queryWrapper = QueryWrapper.create(ShopitemsDO.builder()
                .shopid(data.getShopId())
                .build());
        Page<ShopitemsDO> paginate = shopitemsMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
        return new Page<>(
                paginate.getRecords().stream().map(this::fromShopItemDO).toList(),
                paginate.getPageNumber(),
                paginate.getPageSize(),
                paginate.getTotalRow()
        );
    }

    /**
     * 执行 getShopItem 相关业务逻辑。
     *
     * @param id 记录主键 ID
     * @return ShopItemSearchRtnDTO 类型结果
     */
    public ShopItemSearchRtnDTO getShopItem(Long id) {
        return fromShopItemDO(shopitemsMapper.selectOneById(id));
    }

    /**
     * 执行 modifyShopItem 相关业务逻辑。
     *
     * @param data 业务数据载荷
     * @param isDelete isDelete
     * @return Long 类型结果
     */
    public Long modifyShopItem(ShopItemSearchRtnDTO data, boolean isDelete) {
        Long shopItemId;
        if (isDelete) {
            shopitemsMapper.deleteById(data.getId());
            shopItemId = data.getId();
        } else {
            ShopitemsDO shopitemsDO = ShopitemsDO.builder()
                    .shopitemid(data.getId())
                    .shopid(data.getShopId())
                    .itemid(data.getItemId())
                    .price(data.getPrice())
                    .pitch(data.getPitch())
                    .position(data.getPosition())
                    .build();
            shopitemsMapper.insertOrUpdate(shopitemsDO, true);
            shopItemId = shopitemsDO.getShopitemid();
        }
        ShopFactory.getInstance().reloadShops();
        return shopItemId;
    }

    private ShopItemSearchRtnDTO fromShopItemDO(ShopitemsDO shopitemsDO) {
        Pair<String, String> nameDesc = ItemInformationProvider.getInstance().getNameDesc(shopitemsDO.getItemid());
        return ShopItemSearchRtnDTO.builder()
                .id(shopitemsDO.getShopitemid())
                .shopId(shopitemsDO.getShopid())
                .itemId(shopitemsDO.getItemid())
                .price(shopitemsDO.getPrice())
                .pitch(shopitemsDO.getPitch())
                .position(shopitemsDO.getPosition())
                .itemName(nameDesc == null ? "" : nameDesc.getLeft())
                .itemDesc(nameDesc == null ? "" : nameDesc.getRight())
                .build();
    }
}
