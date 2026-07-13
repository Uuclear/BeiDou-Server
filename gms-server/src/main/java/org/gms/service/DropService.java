package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.DropDataDO;
import org.gms.dao.entity.DropDataGlobalDO;
import org.gms.dao.mapper.DropDataGlobalMapper;
import org.gms.dao.mapper.DropDataMapper;
import org.gms.model.dto.DropSearchReqDTO;
import org.gms.model.dto.DropSearchRtnDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 掉落数据服务类
 * 提供怪物掉落和全局掉落的查询、新增、更新、删除功能，支持按怪物/物品名称模糊搜索，
 * 修改掉落数据后自动清理掉落缓存。
 *
 * @author GMS Server
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class DropService {
    /** 怪物掉落数据访问接口 */
    private final DropDataMapper dropDataMapper;
    /** 全局掉落数据访问接口 */
    private final DropDataGlobalMapper dropDataGlobalMapper;

    /**
     * 分页查询掉落列表
     * 支持查询全局掉落或怪物掉落，支持按大陆、怪物ID/名称、物品ID/名称、任务ID过滤
     *
     * @param data 查询条件
     * @param isGlobal 是否查询全局掉落
     * @return 分页后的掉落结果列表
     */
    public Page<DropSearchRtnDTO> getDropList(DropSearchReqDTO data, boolean isGlobal) {
        if (isGlobal) {
            DropDataGlobalDO dropDataGlobalDO = new DropDataGlobalDO();
            if (data.getContinent() != null) dropDataGlobalDO.setContinent(data.getContinent());
            if (data.getItemId() != null) dropDataGlobalDO.setItemid(data.getItemId());
            if (data.getQuestId() != null) dropDataGlobalDO.setQuestid(data.getQuestId());

            QueryWrapper queryWrapper = QueryWrapper.create(dropDataGlobalDO);
            // 物品名称模糊查询
            if (data.getItemName() != null && !data.getItemName().isEmpty()) {
                List<Integer> itemIds = ItemInformationProvider.getItemsIDsFromName(data.getItemName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!itemIds.isEmpty()) {
                    queryWrapper.and(DropDataGlobalDO::getItemid).in(itemIds);
                } else {
                    // 如果没有匹配的物品，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }

            Page<DropDataGlobalDO> paginate = dropDataGlobalMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
            return new Page<>(
                    paginate.getRecords().stream()
                            .map(record -> DropSearchRtnDTO.builder()
                                    .id(record.getId())
                                    .continent(record.getContinent())
                                    .itemId(record.getItemid())
                                    .itemName(getItemName(record.getItemid()))
                                    .minimumQuantity(record.getMinimumQuantity())
                                    .maximumQuantity(record.getMaximumQuantity())
                                    .questId(record.getQuestid())
                                    .questName(getQuestName(record.getQuestid()))
                                    .chance(record.getChance())
                                    .comments(record.getComments())
                                    .build())
                            .toList(),
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        } else {
            DropDataDO dropDataDO = new DropDataDO();
            if (data.getDropperId() != null) dropDataDO.setDropperid(data.getDropperId());
            if (data.getItemId() != null) dropDataDO.setItemid(data.getItemId());
            if (data.getQuestId() != null) dropDataDO.setQuestid(data.getQuestId());

            QueryWrapper queryWrapper = QueryWrapper.create(dropDataDO);
            // 怪物名称模糊查询
            if (data.getDropperName() != null && !data.getDropperName().isEmpty()) {
                List<Integer> mobIds = MonsterInformationProvider.getMobsIDsFromName(data.getDropperName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!mobIds.isEmpty()) {
                    queryWrapper.and(DropDataDO::getDropperid).in(mobIds);
                } else {
                    // 如果没有匹配的怪物，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }
            // 物品名称模糊查询
            if (data.getItemName() != null && !data.getItemName().isEmpty()) {
                List<Integer> itemIds = ItemInformationProvider.getItemsIDsFromName(data.getItemName())
                        .stream()
                        .map(Pair::getLeft)
                        .toList();
                if (!itemIds.isEmpty()) {
                    queryWrapper.and(DropDataDO::getItemid).in(itemIds);
                } else {
                    // 如果没有匹配的物品，返回空结果
                    return new Page<>(Collections.emptyList(), data.getPageNo(), data.getPageSize(), 0);
                }
            }

            Page<DropDataDO> paginate = dropDataMapper.paginate(data.getPageNo(), data.getPageSize(), queryWrapper);
            return new Page<>(
                    paginate.getRecords().stream()
                            .map(record -> DropSearchRtnDTO.builder()
                                    .id(record.getId())
                                    .dropperId(record.getDropperid())
                                    .dropperName(getMobName(record.getDropperid()))
                                    .itemId(record.getItemid())
                                    .itemName(getItemName(record.getItemid()))
                                    .minimumQuantity(record.getMinimumQuantity())
                                    .maximumQuantity(record.getMaximumQuantity())
                                    .questId(record.getQuestid())
                                    .questName(getQuestName(record.getQuestid()))
                                    .chance(record.getChance())
                                    .build())
                            .toList(),
                    paginate.getPageNumber(),
                    paginate.getPageSize(),
                    paginate.getTotalRow()
            );
        }
    }

    /**
     * 新增/更新/删除掉落数据
     *
     * @param data 掉落数据
     * @param isGlobal 是否为全局掉落
     * @param isDelete 是否为删除操作
     * @return 掉落数据ID
     */
    public Long modifyDropData(DropSearchRtnDTO data, boolean isGlobal, boolean isDelete) {
        Long dropDataId;
        if (isDelete) {
            (isGlobal ? dropDataGlobalMapper : dropDataMapper).deleteById(data.getId());
            dropDataId = data.getId();
        } else {
            if (isGlobal) {
                DropDataGlobalDO dropDataGlobalDO = DropDataGlobalDO.builder()
                        .id(data.getId())
                        .continent(data.getContinent())
                        .itemid(data.getItemId())
                        .minimumQuantity(data.getMinimumQuantity())
                        .maximumQuantity(data.getMaximumQuantity())
                        .questid(data.getQuestId())
                        .chance(data.getChance())
                        .comments(data.getComments())
                        .build();
                dropDataGlobalMapper.insertOrUpdate(dropDataGlobalDO, true);
                dropDataId = dropDataGlobalDO.getId();
            } else {
                DropDataDO dropDataDO = DropDataDO.builder()
                        .id(data.getId())
                        .dropperid(data.getDropperId())
                        .itemid(data.getItemId())
                        .minimumQuantity(data.getMinimumQuantity())
                        .maximumQuantity(data.getMaximumQuantity())
                        .questid(data.getQuestId())
                        .chance(data.getChance())
                        .build();
                dropDataMapper.insertOrUpdate(dropDataDO, true);
                dropDataId = dropDataDO.getId();
            }
        }
        MonsterInformationProvider.getInstance().clearDrops();
        return dropDataId;
    }

    /**
     * 根据物品ID获取物品名称
     *
     * @param itemId 物品ID
     * @return 物品名称
     */
    private String getItemName(Integer itemId) {
        return itemId == null ? null : ItemInformationProvider.getInstance().getName(itemId);
    }

    /**
     * 根据怪物ID获取怪物名称
     *
     * @param mobId 怪物ID
     * @return 怪物名称
     */
    private String getMobName(Integer mobId) {
        return mobId == null ? null : MonsterInformationProvider.getInstance().getMobNameFromId(mobId);
    }

    /**
     * 根据任务ID获取任务名称
     *
     * @param questId 任务ID
     * @return 任务名称
     */
    private String getQuestName(Integer questId) {
        return questId == null ? null : Quest.getInstance(questId).getName();
    }
}
