package org.gms.server.partyquest;

import java.awt.*;

/**
 * 嘉年华守护者（Guardian）刷新点。
 */
public class GuardianSpawnPoint {

    private Point position;
    private boolean taken;
    private int team = -1;

    /**
     * 构造 GuardianSpawnPoint 实例。
     * @param a a
     */
    public GuardianSpawnPoint(Point a) {
        this.position = a;
        this.taken = true;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    public Point getPosition() {
        return position;
    }

    /**
     * 设置位置。
     * @param position 坐标
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 判断是否为Taken。
     * @return boolean 类型结果
     */
    public boolean isTaken() {
        return taken;
    }

    /**
     * 设置Taken。
     * @param taken taken
     */
    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    /**
     * 获取队伍。
     * @return int 类型结果
     */
    public int getTeam() {
        return team;
    }

    /**
     * 设置队伍。
     * @param team team
     */
    public void setTeam(int team) {
        this.team = team;
    }
}
