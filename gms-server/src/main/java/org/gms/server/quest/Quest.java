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
package org.gms.server.quest;

import org.gms.client.Character;
import org.gms.client.QuestStatus;
import org.gms.client.QuestStatus.Status;
import org.gms.config.GameConfig;
import org.gms.constants.game.DelayedQuestUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.quest.actions.AbstractQuestAction;
import org.gms.server.quest.actions.BuffAction;
import org.gms.server.quest.actions.ExpAction;
import org.gms.server.quest.actions.FameAction;
import org.gms.server.quest.actions.InfoAction;
import org.gms.server.quest.actions.ItemAction;
import org.gms.server.quest.actions.MesoAction;
import org.gms.server.quest.actions.NextQuestAction;
import org.gms.server.quest.actions.PetSkillAction;
import org.gms.server.quest.actions.PetSpeedAction;
import org.gms.server.quest.actions.PetTamenessAction;
import org.gms.server.quest.actions.QuestAction;
import org.gms.server.quest.actions.SkillAction;
import org.gms.server.quest.requirements.AbstractQuestRequirement;
import org.gms.server.quest.requirements.BuffExceptRequirement;
import org.gms.server.quest.requirements.BuffRequirement;
import org.gms.server.quest.requirements.CompletedQuestRequirement;
import org.gms.server.quest.requirements.EndDateRequirement;
import org.gms.server.quest.requirements.FieldEnterRequirement;
import org.gms.server.quest.requirements.InfoExRequirement;
import org.gms.server.quest.requirements.InfoNumberRequirement;
import org.gms.server.quest.requirements.IntervalRequirement;
import org.gms.server.quest.requirements.ItemRequirement;
import org.gms.server.quest.requirements.JobRequirement;
import org.gms.server.quest.requirements.MaxLevelRequirement;
import org.gms.server.quest.requirements.MesoRequirement;
import org.gms.server.quest.requirements.MinLevelRequirement;
import org.gms.server.quest.requirements.MinTamenessRequirement;
import org.gms.server.quest.requirements.MobRequirement;
import org.gms.server.quest.requirements.MonsterBookCountRequirement;
import org.gms.server.quest.requirements.NpcRequirement;
import org.gms.server.quest.requirements.PetRequirement;
import org.gms.server.quest.requirements.QuestRequirement;
import org.gms.server.quest.requirements.ScriptRequirement;
import org.gms.util.PacketCreator;
import org.gms.util.StringUtil;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 任务定义与加载中心。从 WZ 数据解析任务条件、奖励动作，管理任务实例缓存、勋章任务及可 exploit 任务黑名单。
 */
public class Quest {
    private static final Logger log = LoggerFactory.getLogger(Quest.class);
    private static volatile Map<Integer, Quest> quests = new HashMap<>();
    private static volatile Map<Integer, Integer> infoNumberQuests = new HashMap<>();
    private static final Map<Short, Integer> medals = new HashMap<>();

    private static final Set<Short> exploitableQuests = new HashSet<>();

    static {
        exploitableQuests.add((short) 2338);    // 存在更多可被利用的任务，需逐一排查
        exploitableQuests.add((short) 3637);
        exploitableQuests.add((short) 3714);
        exploitableQuests.add((short) 21752);
    }

    protected short id;
    protected int timeLimit, timeLimit2;
    protected Map<QuestRequirementType, AbstractQuestRequirement> startReqs = new EnumMap<>(QuestRequirementType.class);
    protected Map<QuestRequirementType, AbstractQuestRequirement> completeReqs = new EnumMap<>(QuestRequirementType.class);
    protected Map<QuestActionType, AbstractQuestAction> startActs = new EnumMap<>(QuestActionType.class);
    protected Map<QuestActionType, AbstractQuestAction> completeActs = new EnumMap<>(QuestActionType.class);
    protected List<Integer> relevantMobs = new LinkedList<>();
    private boolean autoStart;
    private boolean autoPreComplete, autoComplete;
    private boolean repeatable = false;
    private String name = "", parent = "";
    private final static DataProvider questData = DataProviderFactory.getDataProvider(WZFiles.QUEST);
    private final static Data questInfo = questData.getData("QuestInfo.img");
    private final static Data questAct = questData.getData("Act.img");
    private final static Data questReq = questData.getData("Check.img");

    private Quest(int id) {
        this.id = (short) id;

        Data reqData = questReq.getChildByPath(String.valueOf(id));
        if (reqData == null) {//most likely infoEx
            return;
        }

        if (questInfo != null) {
            Data reqInfo = questInfo.getChildByPath(String.valueOf(id));
            if (reqInfo != null) {
                name = DataTool.getString("name", reqInfo, "");
                parent = DataTool.getString("parent", reqInfo, "");

                timeLimit = DataTool.getInt("timeLimit", reqInfo, 0);
                timeLimit2 = DataTool.getInt("timeLimit2", reqInfo, 0);
                autoStart = DataTool.getInt("autoStart", reqInfo, 0) == 1;
                autoPreComplete = DataTool.getInt("autoPreComplete", reqInfo, 0) == 1;
                autoComplete = DataTool.getInt("autoComplete", reqInfo, 0) == 1;

                int medalid = DataTool.getInt("viewMedalItem", reqInfo, 0);
                if (medalid != 0) {
                    medals.put(this.id, medalid);
                }
            } else {
                log.warn("No quest data for id {}", id);
            }
        }

        Data startReqData = reqData.getChildByPath("0");
        if (startReqData != null) {
            for (Data startReq : startReqData.getChildren()) {
                QuestRequirementType type = QuestRequirementType.getByWZName(startReq.getName());
                switch (type) {
                case INTERVAL:
                    repeatable = true;
                    break;
                case MOB:
                    for (Data mob : startReq.getChildren()) {
                        relevantMobs.add(DataTool.getInt(mob.getChildByPath("id")));
                    }
                    break;
                }

                AbstractQuestRequirement req = this.getRequirement(type, startReq);
                if (req == null) {
                    continue;
                }

                startReqs.put(type, req);
            }
        }

        Data completeReqData = reqData.getChildByPath("1");
        if (completeReqData != null) {
            for (Data completeReq : completeReqData.getChildren()) {
                QuestRequirementType type = QuestRequirementType.getByWZName(completeReq.getName());

                AbstractQuestRequirement req = this.getRequirement(type, completeReq);
                if (req == null) {
                    continue;
                }

                if (type.equals(QuestRequirementType.MOB)) {
                    for (Data mob : completeReq.getChildren()) {
                        relevantMobs.add(DataTool.getInt(mob.getChildByPath("id")));
                    }
                }
                completeReqs.put(type, req);
            }
        }
        Data actData = questAct.getChildByPath(String.valueOf(id));
        if (actData == null) {
            return;
        }
        final Data startActData = actData.getChildByPath("0");
        if (startActData != null) {
            for (Data startAct : startActData.getChildren()) {
                QuestActionType questActionType = QuestActionType.getByWZName(startAct.getName());
                AbstractQuestAction act = this.getAction(questActionType, startAct);

                if (act == null) {
                    continue;
                }

                startActs.put(questActionType, act);
            }
        }
        Data completeActData = actData.getChildByPath("1");
        if (completeActData != null) {
            for (Data completeAct : completeActData.getChildren()) {
                QuestActionType questActionType = QuestActionType.getByWZName(completeAct.getName());
                AbstractQuestAction act = this.getAction(questActionType, completeAct);

                if (act == null) {
                    continue;
                }

                completeActs.put(questActionType, act);
            }
        }
    }

    /**
     * 判断是否为Auto、Complete。
     * @return boolean 类型结果
     */
    public boolean isAutoComplete() {
        return autoPreComplete || autoComplete;
    }

    /**
     * 判断是否为Auto、Start。
     * @return boolean 类型结果
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * 获取单例实例。
     * @param id ID
     * @return Quest 类型结果
     */
    public static Quest getInstance(int id) {
        Quest ret = quests.get(id);
        if (ret == null) {
            ret = new Quest(id);
            quests.put(id, ret);
        }
        return ret;
    }

    /**
     * 获取实例来自信息编号。
     * @param infoNumber infoNumber
     * @return Quest 类型结果
     */
    public static Quest getInstanceFromInfoNumber(int infoNumber) {
        Integer id = infoNumberQuests.get(infoNumber);
        if (id == null) {
            id = infoNumber;
        }

        return getInstance(id);
    }

    /**
     * 判断是否为Same、Day、Repeatable。
     * @return boolean 类型结果
     */
    public boolean isSameDayRepeatable() {
        if (!repeatable) {
            return false;
        }

        IntervalRequirement ir = (IntervalRequirement) startReqs.get(QuestRequirementType.INTERVAL);
        return ir.getInterval() < HOURS.toMillis(GameConfig.getServerLong("quest_point_repeatable_interval"));
    }

    /**
     * 判断是否可以Start、任务、按、状态。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean canStartQuestByStatus(Character chr) {
        QuestStatus mqs = chr.getQuest(this);
        return !(!mqs.getStatus().equals(Status.NOT_STARTED) && !(mqs.getStatus().equals(Status.COMPLETED) && repeatable));
    }

    /**
     * 判断是否可以任务、按、信息、Progress。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean canQuestByInfoProgress(Character chr) {
        QuestStatus mqs = chr.getQuest(this);
        List<String> ix = mqs.getInfoEx();
        if (!ix.isEmpty()) {
            short questid = mqs.getQuestID();
            short infoNumber = mqs.getInfoNumber();
            if (infoNumber <= 0) {
                infoNumber = questid;  // on default infoNumber mimics questid
            }

            int ixSize = ix.size();
            for (int i = 0; i < ixSize; i++) {
                String progress = chr.getAbstractPlayerInteraction().getQuestProgress(infoNumber, i);
                String ixProgress = ix.get(i);

                if (!progress.contentEquals(ixProgress)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 判断是否可以Start。
     * @param chr 角色
     * @param npcid NPC ID
     * @return boolean 类型结果
     */
    public boolean canStart(Character chr, int npcid) {
        if (!canStartQuestByStatus(chr)) {
            return false;
        }

        for (AbstractQuestRequirement r : startReqs.values()) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }

        return canQuestByInfoProgress(chr);
    }

    /**
     * 判断是否可以Complete。
     * @param chr 角色
     * @param npcid NPC ID
     * @return boolean 类型结果
     */
    public boolean canComplete(Character chr, Integer npcid) {
        QuestStatus mqs = chr.getQuest(this);
        if (!mqs.getStatus().equals(Status.STARTED)) {
            return false;
        }

        for (AbstractQuestRequirement r : completeReqs.values()) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }

        return canQuestByInfoProgress(chr);
    }

    /**
     * 启动服务/调度器。
     * @param chr 角色
     * @param npc npc
     */
    public void start(Character chr, int npc) {
        if (autoStart || canStart(chr, npc)) {
            Collection<AbstractQuestAction> acts = startActs.values();
            for (AbstractQuestAction a : acts) {
                if (!a.check(chr, null)) { // would null be good ?
                    return;
                }
            }
            for (AbstractQuestAction a : acts) {
                a.run(chr, null);
            }
            forceStart(chr, npc);
        }
    }

    /**
     * 执行 complete 操作。
     * @param chr 角色
     * @param npc npc
     */
    public void complete(Character chr, int npc) {
        complete(chr, npc, null);
    }

    /**
     * 执行 complete 操作。
     * @param chr 角色
     * @param npc npc
     * @param selection selection
     */
    public void complete(Character chr, int npc, Integer selection) {
        if (autoPreComplete || canComplete(chr, npc)) {
            Collection<AbstractQuestAction> acts = completeActs.values();
            for (AbstractQuestAction a : acts) {
                if (!a.check(chr, selection)) {
                    return;
                }
            }
            forceComplete(chr, npc);
            for (AbstractQuestAction a : acts) {
                a.run(chr, selection);
            }
            if (!this.hasNextQuestAction()) {
                chr.announceUpdateQuest(DelayedQuestUpdate.INFO, chr.getQuest(this));
            }
        }
    }

    /**
     * 执行 reset 操作。
     * @param chr 角色
     */
    public void reset(Character chr) {
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.NOT_STARTED);
        chr.updateQuestStatus(newStatus);
    }

    /**
     * 执行 forfeit 操作。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean forfeit(Character chr) {
        if (!chr.getQuest(this).getStatus().equals(Status.STARTED)) {
            return false;
        }
        if (timeLimit > 0) {
            chr.sendPacket(PacketCreator.removeQuestTimeLimit(id));
        }
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.NOT_STARTED);
        newStatus.setForfeited(chr.getQuest(this).getForfeited() + 1);
        chr.updateQuestStatus(newStatus);
        return true;
    }

    /**
     * 执行 force、Start 操作。
     * @param chr 角色
     * @param npc npc
     * @return boolean 类型结果
     */
    public boolean forceStart(Character chr, int npc) {
        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.STARTED, npc);

        QuestStatus oldStatus = chr.getQuest(this.getId());
        for (Entry<Integer, String> e : oldStatus.getProgress().entrySet()) {
            newStatus.setProgress(e.getKey(), e.getValue());
        }

        if (id / 100 == 35 && GameConfig.getServerInt("tot_mob_quest_requirement") > 0) {
            int setProg = 999 - Math.min(999, GameConfig.getServerInt("tot_mob_quest_requirement"));

            for (Integer pid : newStatus.getProgress().keySet()) {
                if (pid >= 8200000 && pid <= 8200012) {
                    String pr = StringUtil.getLeftPaddedStr(Integer.toString(setProg), '0', 3);
                    newStatus.setProgress(pid, pr);
                }
            }
        }

        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompleted(chr.getQuest(this).getCompleted());

        if (timeLimit > 0) {
            newStatus.setExpirationTime(System.currentTimeMillis() + SECONDS.toMillis(timeLimit));
            chr.questTimeLimit(this, timeLimit);
        }
        if (timeLimit2 > 0) {
            newStatus.setExpirationTime(System.currentTimeMillis() + SECONDS.toMillis(timeLimit2));
            chr.questTimeLimit2(this, newStatus.getExpirationTime());
        }

        chr.updateQuestStatus(newStatus);

        return true;
    }

    /**
     * 执行 force、Complete 操作。
     * @param chr 角色
     * @param npc npc
     * @return boolean 类型结果
     */
    public boolean forceComplete(Character chr, int npc) {
        if (timeLimit > 0) {
            chr.sendPacket(PacketCreator.removeQuestTimeLimit(id));
        }

        QuestStatus newStatus = new QuestStatus(this, QuestStatus.Status.COMPLETED, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompleted(chr.getQuest(this).getCompleted());
        newStatus.setCompletionTime(System.currentTimeMillis());
        chr.updateQuestStatus(newStatus);

        chr.sendPacket(PacketCreator.showSpecialEffect(9)); // Quest completion
        chr.getMap().broadcastMessage(chr, PacketCreator.showForeignEffect(chr.getId(), 9), false); //use 9 instead of 12 for both
        return true;
    }

    /**
     * 获取ID。
     * @return short 类型结果
     */
    public short getId() {
        return id;
    }

    /**
     * 获取相关、Mobs。
     * @return List<Integer> 类型结果
     */
    public List<Integer> getRelevantMobs() {
        return relevantMobs;
    }

    /**
     * 获取Start、物品、Amount、Needed。
     * @param itemid 物品 ID
     * @return int 类型结果
     */
    public int getStartItemAmountNeeded(int itemid) {
        AbstractQuestRequirement req = startReqs.get(QuestRequirementType.ITEM);
        if (req == null) {
            return Integer.MIN_VALUE;
        }

        ItemRequirement ireq = (ItemRequirement) req;
        return ireq.getItemAmountNeeded(itemid, false);
    }

    /**
     * 获取Complete、物品、Amount、Needed。
     * @param itemid 物品 ID
     * @return int 类型结果
     */
    public int getCompleteItemAmountNeeded(int itemid) {
        AbstractQuestRequirement req = completeReqs.get(QuestRequirementType.ITEM);
        if (req == null) {
            return Integer.MAX_VALUE;
        }

        ItemRequirement ireq = (ItemRequirement) req;
        return ireq.getItemAmountNeeded(itemid, true);
    }

    /**
     * 获取怪物、Amount、Needed。
     * @param mid mid
     * @return int 类型结果
     */
    public int getMobAmountNeeded(int mid) {
        AbstractQuestRequirement req = completeReqs.get(QuestRequirementType.MOB);
        if (req == null) {
            return 0;
        }

        MobRequirement mreq = (MobRequirement) req;

        return mreq.getRequiredMobCount(mid);
    }

    /**
     * 获取信息编号。
     * @param qs qs
     * @return short 类型结果
     */
    public short getInfoNumber(Status qs) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;

        AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_NUMBER);
        if (req != null) {
            InfoNumberRequirement inReq = (InfoNumberRequirement) req;
            return inReq.getInfoNumber();
        } else {
            return 0;
        }
    }

    /**
     * 获取信息扩展。
     * @param qs qs
     * @param index index
     * @return String 类型结果
     */
    public String getInfoEx(Status qs, int index) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        try {
            AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_EX);
            InfoExRequirement ixReq = (InfoExRequirement) req;
            return ixReq.getInfo().get(index);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取信息扩展。
     * @param qs qs
     * @return List<String> 类型结果
     */
    public List<String> getInfoEx(Status qs) {
        boolean checkEnd = qs.equals(Status.STARTED);
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        try {
            AbstractQuestRequirement req = reqs.get(QuestRequirementType.INFO_EX);
            InfoExRequirement ixReq = (InfoExRequirement) req;
            return ixReq.getInfo();
        } catch (Exception e) {
            return new LinkedList<>();
        }
    }

    /**
     * 获取时间限制。
     * @return int 类型结果
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * 执行 clear、Cache 操作。
     * @param quest 任务
     */
    public static void clearCache(int quest) {
        quests.remove(quest);
    }

    /**
     * 执行 clear、Cache 操作。
     */
    public static void clearCache() {
        quests.clear();
    }

    private AbstractQuestRequirement getRequirement(QuestRequirementType type, Data data) {
        AbstractQuestRequirement ret = null;
        switch (type) {
            case END_DATE:
                ret = new EndDateRequirement(this, data);
                break;
            case JOB:
                ret = new JobRequirement(this, data);
                break;
            case QUEST:
                ret = new QuestRequirement(this, data);
                break;
            case FIELD_ENTER:
                ret = new FieldEnterRequirement(this, data);
                break;
            case INFO_NUMBER:
                ret = new InfoNumberRequirement(this, data);
                break;
            case INFO_EX:
                ret = new InfoExRequirement(this, data);
                break;
            case INTERVAL:
                ret = new IntervalRequirement(this, data);
                break;
            case COMPLETED_QUEST:
                ret = new CompletedQuestRequirement(this, data);
                break;
            case ITEM:
                ret = new ItemRequirement(this, data);
                break;
            case MAX_LEVEL:
                ret = new MaxLevelRequirement(this, data);
                break;
            case MESO:
                ret = new MesoRequirement(this, data);
                break;
            case MIN_LEVEL:
                ret = new MinLevelRequirement(this, data);
                break;
            case MIN_PET_TAMENESS:
                ret = new MinTamenessRequirement(this, data);
                break;
            case MOB:
                ret = new MobRequirement(this, data);
                break;
            case MONSTER_BOOK:
                ret = new MonsterBookCountRequirement(this, data);
                break;
            case NPC:
                ret = new NpcRequirement(this, data);
                break;
            case PET:
                ret = new PetRequirement(this, data);
                break;
            case BUFF:
                ret = new BuffRequirement(this, data);
                break;
            case EXCEPT_BUFF:
                ret = new BuffExceptRequirement(this, data);
                break;
            case SCRIPT:
                ret = new ScriptRequirement(this, data);
                break;
            case NORMAL_AUTO_START:
            case START:
            case END:
                break;
            default:
                //FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, "Unhandled Requirement Type: " + type.toString() + " QuestID: " + this.getId());
                break;
        }
        return ret;
    }

    private AbstractQuestAction getAction(QuestActionType type, Data data) {
        AbstractQuestAction ret = null;
        switch (type) {
            case BUFF:
                ret = new BuffAction(this, data);
                break;
            case EXP:
                ret = new ExpAction(this, data);
                break;
            case FAME:
                ret = new FameAction(this, data);
                break;
            case ITEM:
                ret = new ItemAction(this, data);
                break;
            case MESO:
                ret = new MesoAction(this, data);
                break;
            case NEXTQUEST:
                ret = new NextQuestAction(this, data);
                break;
            case PETSKILL:
                ret = new PetSkillAction(this, data);
                break;
            case QUEST:
                ret = new QuestAction(this, data);
                break;
            case SKILL:
                ret = new SkillAction(this, data);
                break;
            case PETTAMENESS:
                ret = new PetTamenessAction(this, data);
                break;
            case PETSPEED:
                ret = new PetSpeedAction(this, data);
                break;
            case INFO:
                ret = new InfoAction(this, data);
                break;
            default:
                //FilePrinter.printError(FilePrinter.EXCEPTION_CAUGHT, "Unhandled Action Type: " + type.toString() + " QuestID: " + this.getId());
                break;
        }
        return ret;
    }

    /**
     * 执行 restore、Lost、物品 操作。
     * @param chr 角色
     * @param itemid 物品 ID
     * @return boolean 类型结果
     */
    public boolean restoreLostItem(Character chr, int itemid) {
        if (chr.getQuest(this).getStatus().equals(QuestStatus.Status.STARTED)) {
            ItemAction itemAct = (ItemAction) startActs.get(QuestActionType.ITEM);
            if (itemAct != null) {
                return itemAct.restoreLostItem(chr, itemid);
            }
        }

        return false;
    }

    /**
     * 获取勋章需求。
     * @return int 类型结果
     */
    public int getMedalRequirement() {
        Integer medalid = medals.get(id);
        return medalid != null ? medalid : -1;
    }

    /**
     * 获取NPC需求。
     * @param checkEnd checkEnd
     * @return int 类型结果
     */
    public int getNpcRequirement(boolean checkEnd) {
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        AbstractQuestRequirement mqr = reqs.get(QuestRequirementType.NPC);
        if (mqr != null) {
            return ((NpcRequirement) mqr).get();
        } else {
            return -1;
        }
    }

    /**
     * 判断是否拥有脚本需求。
     * @param checkEnd checkEnd
     * @return boolean 类型结果
     */
    public boolean hasScriptRequirement(boolean checkEnd) {
        Map<QuestRequirementType, AbstractQuestRequirement> reqs = !checkEnd ? startReqs : completeReqs;
        AbstractQuestRequirement mqr = reqs.get(QuestRequirementType.SCRIPT);

        if (mqr != null) {
            return ((ScriptRequirement) mqr).get();
        } else {
            return false;
        }
    }

    /**
     * 判断是否拥有下一任务动作。
     * @return boolean 类型结果
     */
    public boolean hasNextQuestAction() {
        Map<QuestActionType, AbstractQuestAction> acts = completeActs;
        AbstractQuestAction mqa = acts.get(QuestActionType.NEXTQUEST);

        return mqa != null;
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    public String getName() {
        return name;
    }

    /**
     * 获取Parent、名称。
     * @return String 类型结果
     */
    public String getParentName() {
        return parent;
    }

    /**
     * 判断是否为Exploitable、任务。
     * @param questid 任务 ID
     * @return boolean 类型结果
     */
    public static boolean isExploitableQuest(short questid) {
        return exploitableQuests.contains(questid);
    }

    /**
     * 获取Matched、Quests。
     * @param search search
     * @return List<Quest> 类型结果
     */
    public static List<Quest> getMatchedQuests(String search) {
        List<Quest> ret = new LinkedList<>();

        search = search.toLowerCase();
        for (Quest mq : quests.values()) {
            if (mq.name.toLowerCase().contains(search) || mq.parent.toLowerCase().contains(search)) {
                ret.add(mq);
            }
        }

        return ret;
    }

    /**
     * 加载所有、Quests。
     */
    public static void loadAllQuests() {
        final Map<Integer, Quest> loadedQuests = new HashMap<>();
        final Map<Integer, Integer> loadedInfoNumberQuests = new HashMap<>();

        for (Data quest : questInfo.getChildren()) {
            int questID = Integer.parseInt(quest.getName());

            Quest q = new Quest(questID);
            loadedQuests.put(questID, q);

            int infoNumber;

            infoNumber = q.getInfoNumber(Status.STARTED);
            if (infoNumber > 0) {
                loadedInfoNumberQuests.put(infoNumber, questID);
            }

            infoNumber = q.getInfoNumber(Status.COMPLETED);
            if (infoNumber > 0) {
                loadedInfoNumberQuests.put(infoNumber, questID);
            }
        }

        Quest.quests = loadedQuests;
        Quest.infoNumberQuests = loadedInfoNumberQuests;
    }

    /**
     * 执行 expire、任务 操作。
     * @param chr 角色
     */
    public void expireQuest(Character chr) {
        if (forfeit(chr)) {
            chr.sendPacket(PacketCreator.questExpire(getId()));
        }
    }
}
