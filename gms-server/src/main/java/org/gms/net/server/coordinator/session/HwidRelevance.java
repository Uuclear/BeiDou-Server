package org.gms.net.server.coordinator.session;

/**
 * HWID关联度记录类
 * 存储硬件ID的关联度评分，用于检测账号共享、多开等异常行为
 *
 * @author OdinMS开发团队
 */
public record HwidRelevance(String hwid, int relevance) {
    /**
     * 获取增加后的关联度（不超过Byte.MAX_VALUE）
     *
     * @return 增加后的关联度值
     */
    public int getIncrementedRelevance() {
        return relevance < Byte.MAX_VALUE ? relevance + 1 : relevance;
    }
}
