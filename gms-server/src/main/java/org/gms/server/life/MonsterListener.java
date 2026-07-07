package org.gms.server.life;

import org.gms.client.Character;

/**
 * 怪物事件监听器接口（死亡、受伤等回调）。
 */
public interface MonsterListener {

    /**
     * 怪物被击杀时的回调。
     * @param aniTime 参数
     */
    void monsterKilled(int aniTime);
    /**
     * 怪物受到伤害时的回调。
     * @param from 参数
     * @param trueDmg 参数
     */
    void monsterDamaged(Character from, int trueDmg);
    /**
     * 怪物被治疗时的回调。
     * @param trueHeal 参数
     */
    void monsterHealed(int trueHeal);
}
