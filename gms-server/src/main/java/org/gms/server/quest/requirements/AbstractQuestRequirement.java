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
import org.gms.server.quest.QuestRequirementType;

/**
 * 任务需求抽象基类，定义 check 与 processData。
 */
public abstract class AbstractQuestRequirement {
    private final QuestRequirementType type;

    /**
     * 执行 抽象任务需求 操作。
     * @param type 类型
     * @return AbstractQuestRequirement 类型结果
     */
    public AbstractQuestRequirement(QuestRequirementType type) {
        this.type = type;
    }

    /**
     * 执行 check 操作。
     * @param chr 角色
     * @param npcid NPC ID
     * @return abstract boolean 类型结果
     */
    public abstract boolean check(Character chr, Integer npcid);

    /**
     * 处理数据。
     * @param data WZ 数据节点
     * @return abstract void 类型结果
     */
    public abstract void processData(Data data);

    /**
     * 获取类型。
     * @return QuestRequirementType 类型结果
     */
    public QuestRequirementType getType() {
        return type;
    }
}