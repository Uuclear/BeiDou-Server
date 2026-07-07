package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeItemsDO;

/**
 * `nxcode_items` 表 / {@link org.gms.dao.entity.NxcodeItemsDO} 的 MyBatis Mapper 接口。
 * <p>
 * NX 兑换码关联道具表，定义每个兑换码可领取的物品与数量。
 */
public interface NxcodeItemsMapper extends BaseMapper<NxcodeItemsDO> {
    /**
     * 级联删除已过期兑换码关联的道具条目。
     */
    @Delete("DELETE FROM nxcode_items WHERE codeid IN (SELECT id FROM nxcode WHERE expiration <= #{timeClear})")
    void clearExpirations(long timeClear);
}
