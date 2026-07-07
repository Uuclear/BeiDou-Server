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
 * 任务脚本对话 API，向 GraalJS 注入为变量 {@code qm}。
 * <p>
 * 继承 {@link NPCConversationManager}，区分任务开始（{@link #isStart()}）与结束阶段，
 * 奖励发放走任务系统 {@link ExpAction}/{@link MesoAction} 以正确更新任务状态。
 * </p>
 *
 * @author RMZero213
 */
public class QuestActionManager extends NPCConversationManager {
    private final boolean start; // this is if the script in question is start or end
    private final int quest;

    public QuestActionManager(Client c, int quest, int npc, boolean start) {
        super(c, npc, null);
        this.quest = quest;
        this.start = start;
    }

/** 获取当前任务 ID */
    public int getQuest() {
        return quest;
    }

/** 当前脚本是否为任务开始阶段 */
    public boolean isStart() {
        return start;
    }

    @Override
/** 销毁事件实例并清理资源 */
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, getClient());
    }

/** forceStartQuest */
    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

/** forceCompleteQuest */
    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }

    // For compatibility with some older scripts...
/** startQuest */
    public void startQuest() {
        forceStartQuest();
    }

    // For compatibility with some older scripts...
/** completeQuest */
    public void completeQuest() {
        forceCompleteQuest();
    }

    @Override
/** gainExp */
    public void gainExp(int gain) {
        ExpAction.runAction(getPlayer(), gain);
    }

    @Override
/** gainMeso */
    public void gainMeso(int gain) {
        MesoAction.runAction(getPlayer(), gain);
    }

    public String getMedalName() {  // usable only for medal quests (id 299XX)
        Quest q = Quest.getInstance(quest);
        return ItemInformationProvider.getInstance().getName(q.getMedalRequirement());
    }
}
