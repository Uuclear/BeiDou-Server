package org.gms.net.server.task;

import lombok.extern.slf4j.Slf4j;
import org.gms.constants.string.ExtendType;
import org.gms.dao.mapper.ExtendValueMapper;
import org.gms.manager.ServerManager;
import org.gms.util.I18nUtil;

@Slf4j
/**
 * 扩展键值定时清理任务，按配置周期清理各业务类型的过期扩展数据。
 */
public class ExtendValueTask implements Runnable {
    @Override
    public void run() {
        ExtendValueMapper extendValueMapper = ServerManager.getApplicationContext().getBean(ExtendValueMapper.class);
        ExtendType.getCleanMap().forEach((key, value) -> {
            try {
                extendValueMapper.clean(key, value);
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("ExtendValueTask.error1"), e);
            }
        });
    }
}
