package org.gms.server.partyquest;

import org.gms.client.Character;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.LinkedList;
import java.util.List;

/**
 * 怪物嘉年华单方队伍状态。
 */
public class MonsterCarnivalParty {

    private List<Character> members = new LinkedList<>();
    private final Character leader;
    private final byte team;
    private int summons = 8;
    private boolean winner = false;

    /**
     * 构造 MonsterCarnivalParty 实例。
     * @param owner 归属角色
     * @param members1 members1（Character 列表/集合）
     * @param team1 team1
     */
    public MonsterCarnivalParty(final Character owner, final List<Character> members1, final byte team1) {
        leader = owner;
        members = members1;
        team = team1;

        for (final Character chr : members) {
            chr.setMonsterCarnivalParty(this);
            chr.setTeam(team);
        }
    }

    /**
     * 获取队长。
     * @return Character 类型结果
     */
    public final Character getLeader() {
        return leader;
    }

    /**
     * 获取成员。
     * @return List<Character> 类型结果
     */
    public List<Character> getMembers() {
        return members;
    }

    /**
     * 获取队伍。
     * @return int 类型结果
     */
    public int getTeam() {
        return team;
    }

    /**
     * 传送Out。
     * @param map 地图名称
     */
    public void warpOut(final int map) {
        for (Character chr : members) {
            chr.changeMap(map, 0);
            chr.setMonsterCarnivalParty(null);
            chr.setMonsterCarnival(null);
        }
        members.clear();
    }

    /**
     * 执行 warp 操作。
     * @param map 地图名称
     * @param portalid portalid
     */
    public void warp(final MapleMap map, final int portalid) {
        for (Character chr : members) {
            chr.changeMap(map, map.getPortal(portalid));
        }
    }

    /**
     * 传送Out。
     */
    public void warpOut() {
        if (winner == true) {
            warpOut(980000003 + (leader.getMonsterCarnival().getRoom() * 100));
        } else {
            warpOut(980000004 + (leader.getMonsterCarnival().getRoom() * 100));
        }
    }

    /**
     * 执行 all在地图 操作。
     * @param map 地图名称
     * @return boolean 类型结果
     */
    public boolean allInMap(MapleMap map) {
        boolean status = true;
        for (Character chr : members) {
            if (chr.getMap() != map) {
                status = false;
            }
        }
        return status;
    }

    /**
     * 移除成员。
     * @param chr 角色
     */
    public void removeMember(Character chr) {
        members.remove(chr);
        chr.changeMap(980000010);
        chr.setMonsterCarnivalParty(null);
        chr.setMonsterCarnival(null);
    }

    /**
     * 判断是否为Winner。
     * @return boolean 类型结果
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * 设置Winner。
     * @param status status
     */
    public void setWinner(boolean status) {
        winner = status;
    }

    /**
     * 执行 display、Match、Result 操作。
     */
    public void displayMatchResult() {
        final String effect = winner ? "quest/carnival/win" : "quest/carnival/lose";

        for (final Character chr : members) {
            chr.sendPacket(PacketCreator.showEffect(effect));
        }
    }

    /**
     * 执行 summon 操作。
     */
    public void summon() {
        this.summons--;
    }

    /**
     * 判断是否可以召唤兽。
     * @return boolean 类型结果
     */
    public boolean canSummon() {
        return this.summons > 0;
    }
}
