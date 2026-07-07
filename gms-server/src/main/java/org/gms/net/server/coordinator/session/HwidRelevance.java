package org.gms.net.server.coordinator.session;

/**
 * HWID 与账号的关联相关度记录，用于防多开策略中的信任度评估。
 *
 * @param hwid      硬件标识
 * @param relevance 当前相关度分值
 */
public record HwidRelevance(String hwid, int relevance) {
    /**
     * 返回递增后的相关度（上限为 Byte.MAX_VALUE）。
     *
     * @return 递增后的相关度
     */
    public int getIncrementedRelevance() {
        return relevance < Byte.MAX_VALUE ? relevance + 1 : relevance;
    }
}
