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
package org.gms.server.quest.actions;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.ItemInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestActionType;

/**
 * 任务施加 Buff 动作。
 */
public class BuffAction extends AbstractQuestAction {
    int itemEffect;

    /**
     * 构造 BuffAction 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public BuffAction(Quest quest, Data data) {
        super(QuestActionType.BUFF, quest);
        processData(data);
    }

    /**
     * 执行 check 操作。
     * @param chr 角色
     * @param extSelection 扩展选项
     * @return boolean 类型结果
     */
    @Override
    public boolean check(Character chr, Integer extSelection) {
        return true;
    }

    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        itemEffect = DataTool.getInt(data);
    }

    /**
     * 执行动作逻辑。
     * @param chr 角色
     * @param extSelection 扩展选项
     */
    @Override
    public void run(Character chr, Integer extSelection) {
        ItemInformationProvider.getInstance().getItemEffect(itemEffect).applyTo(chr);
    }
} 
