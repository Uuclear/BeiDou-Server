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
package org.gms.client.keybind;

/**
 * 键盘按键绑定数据模型，存储按键类型与对应的功能 ID。
 */
public class KeyBinding {
    private final int type;
    private final int action;

    /**
     * 按键绑定
     * @param type 类型
     * @param action action
     */
    public KeyBinding(int type, int action) {
        this.type = type;
        this.action = action;
    }

    /**
     * 获取类型
     * @return 返回值
     */
    public int getType() {
        return type;
    }

    /**
     * 获取Action
     * @return 返回值
     */
    public int getAction() {
        return action;
    }
}
