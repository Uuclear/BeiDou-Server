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
package org.gms.server.quest.actions;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestActionType;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务动作抽象基类，定义 run/processData/check 及职业位编码解析工具。
 */
public abstract class AbstractQuestAction {
    private final QuestActionType type;
    protected int questID;

    /**
     * 构造 AbstractQuestAction 实例。
     * @param action 动作类型
     * @param quest 任务
     */
    public AbstractQuestAction(QuestActionType action, Quest quest) {
        this.type = action;
        this.questID = quest.getId();
    }

    /**
     * 执行动作逻辑。
     * @param chr 角色
     * @param extSelection 扩展选项
     * @return abstract void 类型结果
     */
    public abstract void run(Character chr, Integer extSelection);
    /**
     * 处理数据。
     * @param data WZ 数据节点
     * @return abstract void 类型结果
     */
    public abstract void processData(Data data);

    /**
     * 执行 check 操作。
     * @param chr 角色
     * @param extSelection 扩展选项
     * @return boolean 类型结果
     */
    public boolean check(Character chr, Integer extSelection) {
        return true;
    }

    /**
     * 获取类型。
     * @return QuestActionType 类型结果
     */
    public QuestActionType getType() {
        return type;
    }

    /**
     * 获取职业、By5、字节、Encoding。
     * @param encoded 位编码值
     * @return List<Integer> 类型结果
     */
    public static List<Integer> getJobBy5ByteEncoding(int encoded) {
        List<Integer> ret = new ArrayList<>();
        if ((encoded & 0x1) != 0) {
            ret.add(0);
        }
        if ((encoded & 0x2) != 0) {
            ret.add(100);
        }
        if ((encoded & 0x4) != 0) {
            ret.add(200);
        }
        if ((encoded & 0x8) != 0) {
            ret.add(300);
        }
        if ((encoded & 0x10) != 0) {
            ret.add(400);
        }
        if ((encoded & 0x20) != 0) {
            ret.add(500);
        }
        if ((encoded & 0x400) != 0) {
            ret.add(1000);
        }
        if ((encoded & 0x800) != 0) {
            ret.add(1100);
        }
        if ((encoded & 0x1000) != 0) {
            ret.add(1200);
        }
        if ((encoded & 0x2000) != 0) {
            ret.add(1300);
        }
        if ((encoded & 0x4000) != 0) {
            ret.add(1400);
        }
        if ((encoded & 0x8000) != 0) {
            ret.add(1500);
        }
        if ((encoded & 0x20000) != 0) {
            ret.add(2001); //im not sure of this one
            ret.add(2200);
        }
        if ((encoded & 0x100000) != 0) {
            ret.add(2000);
            ret.add(2001); // 含义尚不确定
        }
        if ((encoded & 0x200000) != 0) {
            ret.add(2100);
        }
        if ((encoded & 0x400000) != 0) {
            ret.add(2001); // 含义尚不确定
            ret.add(2200);
        }

        if ((encoded & 0x40000000) != 0) { //i haven't seen any higher than this o.o
            ret.add(3000);
            ret.add(3200);
            ret.add(3300);
            ret.add(3500);
        }
        return ret;
    }

    /**
     * 获取职业、按、简单、Encoding。
     * @param encoded 位编码值
     * @return List<Integer> 类型结果
     */
    public static List<Integer> getJobBySimpleEncoding(int encoded) {
        List<Integer> ret = new ArrayList<>();
        if ((encoded & 0x1) != 0) {
            ret.add(200);
        }
        if ((encoded & 0x2) != 0) {
            ret.add(300);
        }
        if ((encoded & 0x4) != 0) {
            ret.add(400);
        }
        if ((encoded & 0x8) != 0) {
            ret.add(500);
        }
        return ret;
    }
}
