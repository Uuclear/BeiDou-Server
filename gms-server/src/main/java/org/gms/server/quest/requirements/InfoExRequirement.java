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

import java.util.ArrayList;
import java.util.List;

/**
 * 扩展 info 键值需求。
 */
public class InfoExRequirement extends AbstractQuestRequirement {
    private final List<String> infoExpected = new ArrayList<>();
    private final int questID;


    /**
     * 构造 InfoExRequirement 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public InfoExRequirement(Quest quest, Data data) {
        super(QuestRequirementType.INFO_EX);
        questID = quest.getId();
        processData(data);
    }

    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        // Because we have to...
        for (Data infoEx : data.getChildren()) {
            Data value = infoEx.getChildByPath("value");
            infoExpected.add(DataTool.getString(value, ""));
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
        return true;
    }

    /**
     * 获取信息。
     * @return List<String> 类型结果
     */
    public List<String> getInfo() {
        return infoExpected;
    }
}
