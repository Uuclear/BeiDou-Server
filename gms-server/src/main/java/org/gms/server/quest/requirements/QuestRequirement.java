/*
	This file is part of the MapleSolaxia Maple Story Server

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
package org.gms.server.quest.requirements;

import org.gms.client.Character;
import org.gms.client.QuestStatus;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务需求接口。
 */
public class QuestRequirement extends AbstractQuestRequirement {
    Map<Integer, Integer> quests = new HashMap<>();

    /**
     * 构造 QuestRequirement 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public QuestRequirement(Quest quest, Data data) {
        super(QuestRequirementType.QUEST);
        processData(data);
    }
    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        for (Data questEntry : data.getChildren()) {
            int questID = DataTool.getInt(questEntry.getChildByPath("id"));
            int stateReq = DataTool.getInt(questEntry.getChildByPath("state"));
            quests.put(questID, stateReq);
        }
    }


    /**
     * 执行 check 操作。
     * @param chr 角色
     * @param npcid NPC ID
     * @return boolean 类型结果
     */
    @Override
    public boolean check(Character chr, Integer npcid) {
        for (Integer questID : quests.keySet()) {
            int stateReq = quests.get(questID);
            QuestStatus qs = chr.getQuest(Quest.getInstance(questID));

            if (qs == null && QuestStatus.Status.getById(stateReq).equals(QuestStatus.Status.NOT_STARTED)) {
                continue;
            }

            if (qs == null || !qs.getStatus().equals(QuestStatus.Status.getById(stateReq))) {
                return false;
            }

        }
        return true;
    }
}
