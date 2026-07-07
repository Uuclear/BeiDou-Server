/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.server.events;

import org.gms.client.Character;
import org.gms.client.SkillFactory;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 拯救 Gaga 活动逻辑。
 */
public class RescueGaga extends Events {

    private int completed;

    /**
     * 构造 RescueGaga 实例。
     * @param completed completed
     */
    public RescueGaga(int completed) {
        super();
        this.completed = completed;
    }

    /**
     * 获取已完成。
     * @return int 类型结果
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * 执行 complete 操作。
     */
    public void complete() {
        completed++;
    }

    /**
     * 获取信息。
     * @return int 类型结果
     */
    @Override
    public int getInfo() {
        return getCompleted();
    }

    /**
     * 执行 give、技能 操作。
     * @param chr 角色
     */
    public void giveSkill(Character chr) {
        int skillid = 0;
        switch (chr.getJobType()) {
            case 0:
                skillid = 1013;
                break;
            case 1:
            case 2:
                skillid = 10001014;
        }

        long expiration = (System.currentTimeMillis() + DAYS.toMillis(20));
        if (completed < 20) {
            chr.changeSkillLevel(SkillFactory.getSkill(skillid), (byte) 1, 1, expiration);
            chr.changeSkillLevel(SkillFactory.getSkill(skillid + 1), (byte) 1, 1, expiration);
            chr.changeSkillLevel(SkillFactory.getSkill(skillid + 2), (byte) 1, 1, expiration);
        } else {
            chr.changeSkillLevel(SkillFactory.getSkill(skillid), (byte) 2, 2, chr.getSkillExpiration(skillid));
        }
    }

}
