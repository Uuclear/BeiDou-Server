package org.gms.net.server.coordinator.session;

import org.gms.net.server.coordinator.session.SessionCoordinator.AntiMulticlientResult;

/**
 * 初始化结果类 - 存储会话初始化结果
 *
 * @author OdinMS开发团队
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

    public AntiMulticlientResult getAntiMulticlientResult() {
        return antiMulticlientResult;
    }
}
