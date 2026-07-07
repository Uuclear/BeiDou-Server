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
package org.gms.server.quest.requirements;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

/**
 * 最低等级需求。
 */
public class MinLevelRequirement extends AbstractQuestRequirement {
    private int minLevel;


    /**
     * 构造 MinLevelRequirement 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public MinLevelRequirement(Quest quest, Data data) {
        super(QuestRequirementType.MIN_LEVEL);
        processData(data);
    }


    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        minLevel = DataTool.getInt(data);
    }


    /**
     * 检查角色等级是否达到最低要求。
     * @param chr 角色
     * @param npcid NPC ID（本需求不使用）
     * @return 等级满足时返回 true
     */
    @Override
    public boolean check(Character chr, Integer npcid) {
        return chr.getLevel() >= minLevel;
    }
}
