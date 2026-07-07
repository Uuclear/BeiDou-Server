package org.gms.client;

import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色事件监听器，在角色 HP/MP 或属性变更时触发回调并同步客户端显示。
 */
public class CharacterListener implements AbstractCharacterListener {
    private final Character character;

    /**
     * 构造角色监听器。
     *
     * @param character 被监听的角色实例
     */
    public CharacterListener(Character character) {
        this.character = character;
    }

    /**
     * HP 变更回调，触发 HP 变化后的客户端同步逻辑。
     *
     * @param oldHp 变更前的 HP 值
     */
    @Override
    public void onHpChanged(int oldHp) {
        character.hpChangeAction(oldHp);
    }

    /**
     * HP/MP 上限池更新回调，重新计算本地属性并修正超出上限的 HP/MP。
     */
    @Override
    public void onHpMpPoolUpdate() {
        List<Pair<Stat, Integer>> hpmpupdate = character.recalcLocalStats();
        for (Pair<Stat, Integer> p : hpmpupdate) {
            character.statUpdates.put(p.getLeft(), p.getRight());
        }

        if (character.hp > character.localMaxHp) {
            character.setHp(character.localMaxHp);
            character.statUpdates.put(Stat.HP, character.hp);
        }

        if (character.mp > character.localMaxMp) {
            character.setMp(character.localMaxMp);
            character.statUpdates.put(Stat.MP, character.mp);
        }
    }

    /**
     * 属性更新回调，重新计算角色本地属性。
     */
    @Override
    public void onStatUpdate() {
        character.recalcLocalStats();
    }

    /**
     * 广播属性池更新，将待同步的属性变更打包发送给客户端。
     */
    @Override
    public void onAnnounceStatPoolUpdate() {
        List<Pair<Stat, Integer>> statup = new ArrayList<>(8);
        for (Map.Entry<Stat, Integer> s : character.statUpdates.entrySet()) {
            statup.add(new Pair<>(s.getKey(), s.getValue()));
        }

        character.sendPacket(PacketCreator.updatePlayerStats(statup, true, character));
    }
}
