package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeDO;

/**
 * NX兑换码数据访问Mapper接口，对应数据库表 nxcode。
 * 继承MyBatis-Flex的BaseMapper获得基础CRUD能力。
 *
 * @author sleep
 * @since 2024-05-24
 */
public interface NxcodeMapper extends BaseMapper<NxcodeDO> {
    /**
     * 删除NX兑换码数据
     */
    @Delete("DELETE FROM nxcode WHERE expiration <= #{timeClear}")
    void clearExpirations(long timeClear);
}
