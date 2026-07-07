/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

/**
 * 持有金币需求。
 */
public class MesoRequirement extends AbstractQuestRequirement {
    private int meso = 0;

    /**
     * 构造 MesoRequirement 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public MesoRequirement(Quest quest, Data data) {
        super(QuestRequirementType.MESO);
        processData(data);
    }

    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        meso = DataTool.getInt(data);
    }


    /**
     * 执行 check 操作。
     * @param chr 角色
     * @param npcid NPC ID
     * @return boolean 类型结果
     */
    @Override
    public boolean check(Character chr, Integer npcid) {
        if (chr.getMeso() >= meso) {
            return true;
        } else {
            chr.dropMessage(5, "You don't have enough mesos to complete this quest.");
            return false;
        }
    }
}
