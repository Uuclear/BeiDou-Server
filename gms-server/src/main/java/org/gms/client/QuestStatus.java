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
/**
 * 任务状态数据模型，记录任务的进行状态、完成时间及自定义数据。
 */
public class QuestStatus {
    /**
     * Status枚举，定义相关常量值
     */
    public enum Status {
        UNDEFINED(-1),
        NOT_STARTED(0),
        STARTED(1),
        COMPLETED(2);
        final int status;

        Status(int id) {
            status = id;
        }

        /**
         * 获取ID
         * @return 返回值
         */
        public int getId() {
            return status;
        }

        /**
         * 获取按ID
         * @param id ID
         * @return 返回值
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

    private final short questID;
    private Status status;
    //private boolean updated;   //maybe this can be of use for someone?
    private final Map<Integer, String> progress = new LinkedHashMap<>();
    private final List<Integer> medalProgress = new LinkedList<>();
    private int npc;
    private long completionTime, expirationTime;
    private int forfeited = 0, completed = 0;
    private String customData;

    /**
     * 任务状态
     * @param quest 任务
     * @param status status
     */
    public QuestStatus(Quest quest, Status status) {
        this.questID = quest.getId();
        this.setStatus(status);
        this.completionTime = System.currentTimeMillis();
        this.expirationTime = 0;
        //this.updated = true;
        if (status == Status.STARTED) {
            registerMobs();
        }
    }

    /**
     * 任务状态
     * @param quest 任务
     * @param status status
     * @param npc NPC
     */
    public QuestStatus(Quest quest, Status status, int npc) {
        this.questID = quest.getId();
        this.setStatus(status);
        this.setNpc(npc);
        this.completionTime = System.currentTimeMillis();
        this.expirationTime = 0;
        //this.updated = true;
        if (status == Status.STARTED) {
            registerMobs();
        }
    }

    /**
     * 获取任务
     * @return 返回值
     */
    public Quest getQuest() {
        return Quest.getInstance(questID);
    }

    /**
     * 获取任务ID
     * @return 返回值
     */
    public short getQuestID() {
        return questID;
    }

    /**
     * 获取状态
     * @return 返回值
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 设置状态
     * @param status status
     */
    public final void setStatus(Status status) {
        this.status = status;
    }
    
    /*
    /**
     * wasUpdated
     * @return 返回值
     */
    /**
     * wasUpdated
     * @return 返回值
     */
    public boolean wasUpdated() {
        return updated;
    }
    
    private void setUpdated() {
        this.updated = true;
    }
    
    /**
     * 重置Updated
     */
    public void resetUpdated() {
        this.updated = false;
    }
    */

    public int getNpc() {
        return npc;
    }

    /**
     * 设置NPC
     * @param npc NPC
     */
    public final void setNpc(int npc) {
        this.npc = npc;
    }

    private void registerMobs() {
        for (int i : Quest.getInstance(questID).getRelevantMobs()) {
            progress.put(i, "000");
        }
        //this.setUpdated();
    }

    /**
     * 添加Medal地图
     * @param mapid mapid
     * @return 返回值
     */
    public boolean addMedalMap(int mapid) {
        if (medalProgress.contains(mapid)) {
            return false;
        }
        medalProgress.add(mapid);
        //this.setUpdated();
        return true;
    }

    /**
     * 获取MedalProgress
     * @return 返回值
     */
    public int getMedalProgress() {
        return medalProgress.size();
    }

    /**
     * 获取MedalMaps
     * @return 返回值
     */
    public List<Integer> getMedalMaps() {
        return medalProgress;
    }

    /**
     * progress
     * @param id ID
     * @return 返回值
     */
    /**
     * progress
     * @param id ID
     * @return 返回值
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
        //this.setUpdated();
        return true;
    }

    /**
     * 设置Progress
     * @param id ID
     * @param pr pr
     */
    public void setProgress(int id, String pr) {
        progress.put(id, pr);
        //this.setUpdated();
    }

    /**
     * madeProgress
     * @return 返回值
     */
    /**
     * madeProgress
     * @return 返回值
     */
    public boolean madeProgress() {
        return progress.size() > 0;
    }

    /**
     * 获取Progress
     * @param id ID
     * @return 返回值
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
     * 重置Progress
     * @param id ID
     */
    public void resetProgress(int id) {
        setProgress(id, "000");
    }

    /**
     * 重置全部Progress
     */
    public void resetAllProgress() {
        for (Map.Entry<Integer, String> entry : progress.entrySet()) {
            setProgress(entry.getKey(), "000");
        }
    }

    /**
     * 获取Progress
     * @return 返回值
     */
    public Map<Integer, String> getProgress() {
        return Collections.unmodifiableMap(progress);
    }

    /**
     * 获取信息Number
     * @return 返回值
     */
    public short getInfoNumber() {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoNumber(s);
    }

    /**
     * 获取信息Ex
     * @param index index
     * @return 返回值
     */
    public String getInfoEx(int index) {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoEx(s, index);
    }

    /**
     * 获取信息Ex
     * @return 返回值
     */
    public List<String> getInfoEx() {
        Quest q = this.getQuest();
        Status s = this.getStatus();

        return q.getInfoEx(s);
    }

    /**
     * 获取CompletionTime
     * @return 返回值
     */
    public long getCompletionTime() {
        return completionTime;
    }

    /**
     * 设置CompletionTime
     * @param completionTime completionTime
     */
    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * 获取过期时间Time
     * @return 返回值
     */
    public long getExpirationTime() {
        return expirationTime;
    }

    /**
     * 设置过期时间Time
     * @param expirationTime expirationTime
     */
    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * 获取Forfeited
     * @return 返回值
     */
    public int getForfeited() {
        return forfeited;
    }

    /**
     * 获取Completed
     * @return 返回值
     */
    public int getCompleted() {
        return completed;
    }

    /**
     * 设置Forfeited
     * @param forfeited forfeited
     */
    public void setForfeited(int forfeited) {
        if (forfeited >= this.forfeited) {
            this.forfeited = forfeited;
        } else {
            throw new IllegalArgumentException("Can't set forfeits to something lower than before.");
        }
    }

    /**
     * 设置Completed
     * @param completed completed
     */
    public void setCompleted(int completed) {
        if (completed >= this.completed) {
            this.completed = completed;
        } else {
            throw new IllegalArgumentException("Can't set completes to something lower than before.");
        }
    }

    /**
     * 设置Custom数据
     * @param customData customData
     */
    public final void setCustomData(final String customData) {
        this.customData = customData;
    }

    /**
     * 获取Custom数据
     * @return 返回值
     */
    public final String getCustomData() {
        return customData;
    }

    /**
     * 获取Progress数据
     * @return 返回值
     */
    public String getProgressData() {
        StringBuilder str = new StringBuilder();
        for (String ps : progress.values()) {
            str.append(ps);
        }
        return str.toString();
    }
}
