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
package org.gms.server.quest.actions;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestActionType;

/**
 * 任务设置 info 编号动作（用于 UI 展示进度）。
 */
public class InfoAction extends AbstractQuestAction {

    private String info;
    private final int questID;

    /**
     * 构造 InfoAction 实例。
     * @param quest 任务
     * @param data WZ 数据节点
     */
    public InfoAction(Quest quest, Data data) {
        super(QuestActionType.INFO, quest);
        questID = quest.getId();
        processData(data);
    }

    /**
     * 处理数据。
     * @param data WZ 数据节点
     */
    @Override
    public void processData(Data data) {
        info = DataTool.getString(data, "");
    }


    /**
     * 执行动作逻辑。
     * @param chr 角色
     * @param extSelection 扩展选项
     */
    @Override
    public void run(Character chr, Integer extSelection) {
        chr.getAbstractPlayerInteraction().setQuestProgress(questID, info);
    }

}
