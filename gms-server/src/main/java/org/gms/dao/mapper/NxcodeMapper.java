package org.gms.dao.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.gms.dao.entity.NxcodeDO;

/**
 * `nxcode` 表 / {@link org.gms.dao.entity.NxcodeDO} 的 MyBatis Mapper 接口。
 * <p>
 * NX 兑换码主表，存储兑换码字符串、有效期及可兑换次数限制。
 */
public interface NxcodeMapper extends BaseMapper<NxcodeDO> {
    /**
     * 删除已过期的 NX 兑换码记录。
     */
    @Delete("DELETE FROM nxcode WHERE expiration <= #{timeClear}")
    void clearExpirations(long timeClear);
}
