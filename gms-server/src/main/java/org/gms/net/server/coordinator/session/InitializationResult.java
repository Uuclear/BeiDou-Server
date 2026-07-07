package org.gms.net.server.coordinator.session;

import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;

/**
 * 会话初始化结果枚举，映射到防多开检测结果。
 */
enum InitializationResult {
    SUCCESS(AntiMulticlientResult.SUCCESS),
    ALREADY_INITIALIZED(AntiMulticlientResult.REMOTE_PROCESSING),
    TIMED_OUT(AntiMulticlientResult.COORDINATOR_ERROR),
    ERROR(AntiMulticlientResult.COORDINATOR_ERROR);

    private final AntiMulticlientResult antiMulticlientResult;

    InitializationResult(AntiMulticlientResult antiMulticlientResult) {
        this.antiMulticlientResult = antiMulticlientResult;
    }

    /**
     * 返回对应的防多开检测结果。
     *
     * @return 防多开结果枚举
     */
    public AntiMulticlientResult getAntiMulticlientResult() {
        return antiMulticlientResult;
    }
}
