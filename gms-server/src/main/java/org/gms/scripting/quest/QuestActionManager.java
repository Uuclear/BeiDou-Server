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
package org.gms.scripting.quest;

import org.gms.client.Client;
import org.gms.scripting.npc.NPCConversationManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.server.quest.actions.ExpAction;
import org.gms.server.quest.actions.MesoAction;

/**
 * 任务动作管理器，继承自NPCConversationManager，
 * 为任务脚本提供与玩家交互和任务操作的专用方法。
 * 管理任务的开始和完成流程，提供经验值、金币等奖励发放功能。
 *
 * @author RMZero213
 */
public class QuestActionManager extends NPCConversationManager {
    /**
     * 标记当前是任务开始(true)还是任务完成(false)阶段
     */
    private final boolean start;
    
    /**
     * 当前任务ID
     */
    private final int quest;

    /**
     * 构造任务动作管理器
     *
     * @param c 客户端连接对象
     * @param quest 任务ID
     * @param npc 交互的NPC ID
     * @param start true表示任务开始阶段，false表示任务完成阶段
     */
    public QuestActionManager(Client c, int quest, int npc, boolean start) {
        super(c, npc, null);
        this.quest = quest;
        this.start = start;
    }

    /**
     * 获取当前任务ID
     *
     * @return 任务ID
     */
    public int getQuest() {
        return quest;
    }

    /**
     * 判断是否为任务开始阶段
     *
     * @return true表示任务开始阶段，false表示任务完成阶段
     */
    public boolean isStart() {
        return start;
    }

    /**
     * 释放资源，清理任务脚本上下文
     */
    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, getClient());
    }

    /**
     * 强制开始当前任务（使用构造时传入的quest ID）
     *
     * @return 任务成功开始返回true
     */
    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

    /**
     * 强制完成当前任务（使用构造时传入的quest ID）
     *
     * @return 任务成功完成返回true
     */
    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }

    /**
     * 开始任务的兼容方法，为旧脚本提供兼容性支持
     */
    public void startQuest() {
        forceStartQuest();
    }

    /**
     * 完成任务的兼容方法，为旧脚本提供兼容性支持
     */
    public void completeQuest() {
        forceCompleteQuest();
    }

    /**
     * 给玩家发放经验值奖励
     *
     * @param gain 经验值数量
     */
    @Override
    public void gainExp(int gain) {
        ExpAction.runAction(getPlayer(), gain);
    }

    /**
     * 给玩家发放金币奖励
     *
     * @param gain 金币数量
     */
    @Override
    public void gainMeso(int gain) {
        MesoAction.runAction(getPlayer(), gain);
    }

    /**
     * 获取勋章任务的勋章名称。
     * 仅适用于勋章任务（ID为299XX的任务）。
     *
     * @return 勋章物品的名称
     */
    public String getMedalName() {
        Quest q = Quest.getInstance(quest);
        return ItemInformationProvider.getInstance().getName(q.getMedalRequirement());
    }
}
