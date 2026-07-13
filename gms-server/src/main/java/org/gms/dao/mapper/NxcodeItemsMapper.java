package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeItemsDO;

/**
 * NX兑换码物品数据访问Mapper接口，对应数据库表 nxcode_items。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface NxcodeItemsMapper extends BaseMapper<NxcodeItemsDO> {
    /**
     * 删除NX兑换码物品数据
     */
    @Delete("DELETE FROM nxcode_items WHERE codeid IN (SELECT id FROM nxcode WHERE expiration <= #{timeClear})")
    void clearExpirations(long timeClear);
}
