/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.client.autoban;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 自动封禁管理器，跟踪玩家可疑行为积分并在超阈值时触发自动封禁。
 */
public class AutobanManager {
    private static final Logger log = LoggerFactory.getLogger(AutobanManager.class);

    private final Character chr;
    private final Map<AutobanFactory, Integer> points = new HashMap<>();
    private final Map<AutobanFactory, Long> lastTime = new HashMap<>();
    private int misses = 0;
    private int lastmisses = 0;
    private int samemisscount = 0;
    private final long[] spam = new long[20];
    private final int[] timestamp = new int[20];
    private final byte[] timestampcounter = new byte[20];


    /**
     * 构造自动封禁管理器。
     *
     * @param chr 被监控的角色
     */
    public AutobanManager(Character chr) {
        this.chr = chr;
    }

    /**
     * 为指定封禁类型累加可疑积分，达到阈值时触发自动封禁。
     *
     * @param fac 封禁检测类型
     * @param reason 触发原因描述
     */
    public void addPoint(AutobanFactory fac, String reason) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            if (chr.isGM() || chr.isBanned()) {
                return;
            }

            // 检查该类型是否被禁用
            if (fac.isDisabled()) {
                return;
            }

            // 获取生效的过期时间
            long effectiveExpire = fac.getEffectiveExpiretime();

            if (lastTime.containsKey(fac)) {
                if (lastTime.get(fac) < (Server.getInstance().getCurrentTime() - effectiveExpire)) {
                    points.put(fac, points.get(fac) / 2); //So the points are not completely gone.
                }
            }
            if (effectiveExpire != -1) {
                lastTime.put(fac, Server.getInstance().getCurrentTime());
            }

            if (points.containsKey(fac)) {
                points.put(fac, points.get(fac) + 1);
            } else {
                points.put(fac, 1);
            }

            // 获取生效的积分阈值
            int effectivePoints = fac.getEffectivePoints();
            if (points.get(fac) >= effectivePoints) {
                chr.autoBan(reason);
            }
        }
        if (GameConfig.getServerBoolean("use_auto_ban_log")) {
            // Lets log every single point too.
            log.info("Autoban - chr {} caused {} {}", Character.makeMapleReadable(chr.getName()), fac.name(), reason);
        }
    }

    /**
     * 记录一次未命中（疑似无敌作弊）。
     */
    public void addMiss() {
        this.misses++;
    }

    /**
     * 重置未命中计数，连续多次未命中时断开连接。
     */
    public void resetMisses() {
        if (lastmisses == misses && misses > 6) {
            samemisscount++;
        }
        if (samemisscount > 4) {
            chr.sendPolice("You will be disconnected for miss godmode.");
        }
        //chr.autoban("Autobanned for : " + misses + " Miss godmode", 1);
        else if (samemisscount > 0) {
            this.lastmisses = misses;
        }
        this.misses = 0;
    }

    //Don't use the same type for more than 1 thing
    /**
     * 记录指定类型的刷屏时间戳（使用当前服务器时间）。
     *
     * @param type 刷屏检测类型索引
     */
    public void spam(int type) {
        this.spam[type] = Server.getInstance().getCurrentTime();
    }

    /**
     * 记录指定类型的刷屏时间戳（使用给定时间值）。
     *
     * @param type 刷屏检测类型索引
     * @param timestamp 时间戳
     */
    public void spam(int type, int timestamp) {
        this.spam[type] = timestamp;
    }

    /**
     * 获取指定类型最近一次刷屏的时间戳。
     *
     * @param type 刷屏检测类型索引
     * @return 最近一次刷屏时间戳
     */
    public long getLastSpam(int type) {
        return spam[type];
    }

    /**
     * 时间戳刷屏检测器，同一秒内重复操作超过阈值则断开连接。
     * <p>
     * type 含义：<br>
     * 1: 宠物喂食 &nbsp; 2: 背包合并 &nbsp; 3: 背包排序 &nbsp; 4: 特殊移动<br>
     * 5: 捕捉道具 &nbsp; 6: 物品丢弃 &nbsp; 7: 聊天 &nbsp; 8: HP 持续恢复 &nbsp; 9: MP 持续恢复
     *
     * @param type 检测类型索引
     * @param time 当前时间戳（秒）
     * @param times 允许的最大重复次数
     */
    public void setTimestamp(int type, int time, int times) {
        if (this.timestamp[type] == time) {
            this.timestampcounter[type]++;
            if (this.timestampcounter[type] >= times) {
                if (GameConfig.getServerBoolean("use_auto_ban")) {
                    chr.getClient().disconnect(false, false);
                }

                log.info("Autoban - Chr {} was caught spamming TYPE {} and has been disconnected", chr, type);
            }
        } else {
            this.timestamp[type] = time;
            this.timestampcounter[type] = 0;
        }
    }
}
