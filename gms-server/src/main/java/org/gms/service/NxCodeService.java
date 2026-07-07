package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.dao.mapper.NxcodeItemsMapper;
import org.gms.dao.mapper.NxcodeMapper;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 点券兑换码业务服务，管理 NX 兑换码的生成与使用记录。
 */
@Service
@AllArgsConstructor
public class NxCodeService {
    private final NxcodeMapper nxcodeMapper;
    private final NxcodeItemsMapper nxcodeItemsMapper;

    /**
     * 执行 clearExpirations 相关业务逻辑。
     */
    public void clearExpirations() {
        long timeClear = System.currentTimeMillis() - DAYS.toMillis(14);
        nxcodeItemsMapper.clearExpirations(timeClear);
        nxcodeMapper.clearExpirations(timeClear);
    }

}
