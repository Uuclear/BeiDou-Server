package org.gms.model.pojo;

/**
 * 技能条目 POJO，表示角色单个技能的运行时等级信息，用于技能列表序列化与展示。
 */
public class SkillEntry {
    /**
     * 技能主等级（技能书可提升的上限）。
     */
    public int masterLevel;
    /**
     * 当前已学技能等级。
     */
    public byte skillLevel;
    /**
     * 技能有效期时间戳，0 表示永久。
     */
    public long expiration;

    /**
     * 构造指定等级与过期时间的技能条目。
     */
    public SkillEntry(byte skillLevel, int masterLevel, long expiration) {
        this.skillLevel = skillLevel;
        this.masterLevel = masterLevel;
        this.expiration = expiration;
    }

    /**
     * 以「当前等级:主等级」格式返回简要字符串。
     */
    @Override
    public String toString() {
        return skillLevel + ":" + masterLevel;
    }
}
