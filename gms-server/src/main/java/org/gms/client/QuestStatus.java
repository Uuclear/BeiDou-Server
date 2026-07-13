/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.client;

import org.gms.server.quest.Quest;
import org.gms.util.StringUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 任务状态类
 * 管理单个任务的状态信息，包括进度、完成时间、自定义数据等
 *
 * @author Matze
 */
public class QuestStatus {
    /**
     * 任务状态枚举
     */
    public enum Status {
        /** 未定义 */
        UNDEFINED(-1),
        /** 未开始 */
        NOT_STARTED(0),
        /** 进行中 */
        STARTED(1),
        /** 已完成 */
        COMPLETED(2);

        /** 状态ID */
        final int status;

        Status(int id) {
            status = id;
        }

        /**
         * 获取状态ID
         * @return 状态ID
         */
        public int getId() {
            return status;
        }

        /**
         * 根据ID获取状态枚举
         * @param id 状态ID
         * @return 对应的Status枚举
         */
        public static Status getById(int id) {
            for (Status l : Status.values()) {
                if (l.getId() == id) {
                    return l;
                }
            }
            return null;
        }
    }

    /** 任务ID */
    private final short questID;
    /** 任务状态 */
    private Status status;
    /** 任务进度（怪物击杀数等），key为怪物ID，value为3位数字符串格式的计数 */
    private final Map<Integer, String> progress = new LinkedHashMap<>();
    /** 勋章任务进度（已访问地图列表） */
    private final List<Integer> medalProgress = new LinkedList<>();
    /** 关联NPC ID */
    private int npc;
    /** 完成时间 */
    private long completionTime;
    /** 过期时间 */
    private long expirationTime;
    /** 放弃次数 */
    private int forfeited = 0;
    /** 完成次数 */
    private int completed = 0;
    /** 自定义数据 */
    private String customData;

    /**
     * 构造函数
     * @param quest 任务对象
     * @param status 任务状态
     */
    public QuestStatus(Quest quest, Status status) {
        this.questID = quest.getId();
        this.setStatus(status);
        this.completionTime = System.currentTimeMillis();
        this.expirationTime = 0;
        if (status == Status.STARTED) {
            registerMobs();
        }
    }

    /**
     * 构造函数（带NPC参数）
     * @param quest 任务对象
     * @param status 任务状态
     * @param npc 关联NPC ID
     */
    public QuestStatus(Quest quest, Status status, int npc) {
        this.questID = quest.getId();
        this.setStatus(status);
        this.setNpc(npc);
        this.completionTime = System.currentTimeMillis();
        this.expirationTime = 0;
        if (status == Status.STARTED) {
            registerMobs();
        }
    }

    /**
     * 获取任务对象
     * @return 任务对象
     */
    public Quest getQuest() {
        return Quest.getInstance(questID);
    }

    /**
     * 获取任务ID
     * @return 任务ID
     */
    public short getQuestID() {
        return questID;
    }

    /**
     * 获取任务状态
     * @return 任务状态
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 设置任务状态
     * @param status 任务状态
     */
    public final void setStatus(Status status) {
        this.status = status;
    }

    /**
     * 获取关联NPC ID
     * @return NPC ID
     */
    public int getNpc() {
        return npc;
    }

    /**
     * 设置关联NPC ID
     * @param npc NPC ID
     */
    public final void setNpc(int npc) {
        this.npc = npc;
    }

    /**
     * 注册任务相关怪物，初始化进度
     */
    private void registerMobs() {
        for (int i : Quest.getInstance(questID).getRelevantMobs()) {
            progress.put(i, "000");
        }
    }

    /**
     * 添加勋章地图进度
     * @param mapid 地图ID
     * @return 是否添加成功（如果已存在则返回false）
     */
    public boolean addMedalMap(int mapid) {
        if (medalProgress.contains(mapid)) {
            return false;
        }
        medalProgress.add(mapid);
        return true;
    }

    /**
     * 获取勋章进度数量
     * @return 已完成地图数
     */
    public int getMedalProgress() {
        return medalProgress.size();
    }

    /**
     * 获取勋章地图列表
     * @return 已访问地图ID列表
     */
    public List<Integer> getMedalMaps() {
        return medalProgress;
    }

    /**
     * 更新怪物击杀进度
     * @param id 怪物ID
     * @return 是否成功更新
     */
    public boolean progress(int id) {
        String currentStr = progress.get(id);
        if (currentStr == null) {
            return false;
        }

        int current = Integer.parseInt(currentStr);
        if (current >= this.getQuest().getMobAmountNeeded(id)) {
            return false;
        }

        String str = StringUtil.getLeftPaddedStr(Integer.toString(++current), '0', 3);
        progress.put(id, str);
        return true;
    }

    /**
     * 设置特定ID的进度值
     * @param id 怪物ID或其他标识
     * @param pr 进度值（3位字符串格式）
     */
    public void setProgress(int id, String pr) {
        progress.put(id, pr);
    }

    /**
     * 判断是否有进度记录
     * @return 是否有进度
     */
    public boolean madeProgress() {
        return progress.size() > 0;
    }

    /**
     * 获取特定ID的进度值
     * @param id 怪物ID或其他标识
     * @return 进度值字符串
     */
    public String getProgress(int id) {
        String ret = progress.get(id);
        if (ret == null) {
            return "";
        } else {
            return ret;
        }
    }

    /**
     * 重置特定ID的进度为0
     * @param id 怪物ID或其他标识
     */
    public void resetProgress(int id) {
        setProgress(id, "000");
    }

    /**
     * 重置所有进度
     */
    public void resetAllProgress() {
        for (Map.Entry<Integer, String> entry : progress.entrySet()) {
            setProgress(entry.getKey(), "000");
        }
    }

    /**
     * 获取进度Map（不可修改视图）
     * @return 进度Map
     */
    public Map<Integer, String> getProgress() {
        return Collections.unmodifiableMap(progress);
    }

    /**
     * 获取任务信息编号
     * @return 信息编号
     */
    public short getInfoNumber() {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoNumber(s);
    }

    /**
     * 获取指定索引的扩展信息
     * @param index 索引
     * @return 扩展信息字符串
     */
    public String getInfoEx(int index) {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoEx(s, index);
    }

    /**
     * 获取所有扩展信息
     * @return 扩展信息列表
     */
    public List<String> getInfoEx() {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoEx(s);
    }

    /**
     * 获取完成时间
     * @return 完成时间戳
     */
    public long getCompletionTime() {
        return completionTime;
    }

    /**
     * 设置完成时间
     * @param completionTime 完成时间戳
     */
    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * 获取过期时间
     * @return 过期时间戳
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * 设置过期时间
     * @param expirationTime 过期时间戳
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * 获取放弃次数
     * @return 放弃次数
     */
    public int getForfeited() {
        return forfeited;
    }

    /**
     * 获取完成次数
     * @return 完成次数
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * 设置放弃次数（只能递增）
     * @param forfeited 放弃次数
     */
    public void setForfeited(int forfeited) {
        if (forfeited >= this.forfeited) {
            this.forfeited = forfeited;
        } else {
            throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
        }
    }

    /**
     * 设置完成次数（只能递增）
     * @param completed 完成次数
     */
    public void setCompleted(int completed) {
        if (completed >= this.completed) {
            this.completed = completed;
        } else {
            throw new IllegalArgumentException("Can't set completes to something lower than before.");
        }
    }

    /**
     * 设置自定义数据
     * @param customData 自定义数据字符串
     */
    public final void setCustomData(final String customData) {
        this.customData = customData;
    }

    /**
     * 获取自定义数据
     * @return 自定义数据字符串
     */
    public final String getCustomData() {
        return customData;
    }

    /**
     * 获取进度数据的拼接字符串
     * @return 所有进度值拼接的字符串
     */
    public String getProgressData() {
        StringBuilder str = new StringBuilder();
        for (String ps : progress.values()) {
            str.append(ps);
        }
        return str.toString();
    }
}
