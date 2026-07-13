package org.gms.model.pojo;

/**
 * 技能条目实体类
 * 用于表示角色的单个技能信息，包括技能等级、精通等级和过期时间
 */
public class SkillEntry {
    /**
     * 技能精通等级（最高可提升等级）
     */
    public int masterLevel;

    /**
     * 当前技能等级
     */
    public byte skillLevel;

    /**
     * 技能过期时间戳（毫秒），0表示永久有效
     */
    public long expiration;

    /**
     * 构造函数
     * @param skillLevel 当前技能等级
     * @param masterLevel 技能精通等级
     * @param expiration 技能过期时间戳
     */
    public SkillEntry(byte skillLevel, int masterLevel, long expiration) {
        this.skillLevel = skillLevel;
        this.masterLevel = masterLevel;
        this.expiration = expiration;
    }

    /**
     * 返回技能等级和精通等级的字符串表示
     * @return 格式为 "skillLevel:masterLevel" 的字符串
     */
    @Override
    public String toString() {
        return skillLevel + ":" + masterLevel;
    }
}
