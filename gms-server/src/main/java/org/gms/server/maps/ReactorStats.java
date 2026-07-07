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
package org.gms.server.maps;

import org.gms.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反应堆静态属性（状态数、掉落表、技能等）。
 */
public class ReactorStats {
    private Point tl;
    private Point br;
    private final Map<Byte, List<StateData>> stateInfo = new HashMap<>();
    private final Map<Byte, Integer> timeoutInfo = new HashMap<>();

    /**
     * 设置TL。
     * @param tl tl
     */
    public void setTL(Point tl) {
        this.tl = tl;
    }

    /**
     * 设置BR。
     * @param br br
     */
    public void setBR(Point br) {
        this.br = br;
    }

    /**
     * 获取TL。
     * @return Point 类型结果
     */
    public Point getTL() {
        return tl;
    }

    /**
     * 获取BR。
     * @return Point 类型结果
     */
    public Point getBR() {
        return br;
    }

    /**
     * 添加状态。
     * @param state 状态值
     * @param data WZ 数据节点（StateData 列表/集合）
     * @param timeOut timeOut
     */
    public void addState(byte state, List<StateData> data, int timeOut) {
        stateInfo.put(state, data);
        if (timeOut > -1) {
            timeoutInfo.put(state, timeOut);
        }
    }

    /**
     * 添加状态。
     * @param state 状态值
     * @param type 类型
     * @param reactItem reactItem
     * @param nextState nextState
     * @param timeOut timeOut
     * @param canTouch canTouch
     */
    public void addState(byte state, int type, Pair<Integer, Integer> reactItem, byte nextState, int timeOut, byte canTouch) {
        List<StateData> data = new ArrayList<>();
        data.add(new StateData(type, reactItem, null, nextState));
        stateInfo.put(state, data);
    }

    /**
     * 获取Timeout。
     * @param state 状态值
     * @return int 类型结果
     */
    public int getTimeout(byte state) {
        Integer i = timeoutInfo.get(state);
        return (i == null) ? -1 : i;
    }

    /**
     * 获取Timeout、状态。
     * @param state 状态值
     * @return byte 类型结果
     */
    public byte getTimeoutState(byte state) {
        return stateInfo.get(state).get(stateInfo.get(state).size() - 1).getNextState();
    }

    /**
     * 获取状态、Size。
     * @param state 状态值
     * @return byte 类型结果
     */
    public byte getStateSize(byte state) {
        return (byte) stateInfo.get(state).size();
    }

    /**
     * 获取下一状态。
     * @param state 状态值
     * @param index index
     * @return byte 类型结果
     */
    public byte getNextState(byte state, byte index) {
        if (stateInfo.get(state) == null || stateInfo.get(state).size() < (index + 1)) {
            return -1;
        }
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getNextState();
        } else {
            return -1;
        }
    }

    /**
     * 获取活动、Skills。
     * @param state 状态值
     * @param index index
     * @return List<Integer> 类型结果
     */
    public List<Integer> getActiveSkills(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getActiveSkills();
        } else {
            return null;
        }
    }

    /**
     * 获取类型。
     * @param state 状态值
     * @return int 类型结果
     */
    public int getType(byte state) {
        List<StateData> list = stateInfo.get(state);
        if (list != null) {
            return list.get(0).getType();
        } else {
            return -1;
        }
    }

    /**
     * 获取React、物品。
     * @param state 状态值
     * @param index index
     * @return Pair<Integer, Integer> 类型结果
     */
    public Pair<Integer, Integer> getReactItem(byte state, byte index) {
        StateData nextState = stateInfo.get(state).get(index);
        if (nextState != null) {
            return nextState.getReactItem();
        } else {
            return null;
        }
    }


    public static class StateData {
        private final int type;
        private final Pair<Integer, Integer> reactItem;
        private final List<Integer> activeSkills;
        private final byte nextState;

        /**
         * 执行 状态数据 操作。
         * @param type 类型
         * @param reactItem reactItem
         * @param activeSkills activeSkills（Integer 列表/集合）
         * @param nextState nextState
         * @return StateData 类型结果
         */
        public StateData(int type, Pair<Integer, Integer> reactItem, List<Integer> activeSkills, byte nextState) {
            this.type = type;
            this.reactItem = reactItem;
            this.activeSkills = activeSkills;
            this.nextState = nextState;
        }

        private int getType() {
            return type;
        }

        private byte getNextState() {
            return nextState;
        }

        private Pair<Integer, Integer> getReactItem() {
            return reactItem;
        }

        private List<Integer> getActiveSkills() {
            return activeSkills;
        }
    }
}
