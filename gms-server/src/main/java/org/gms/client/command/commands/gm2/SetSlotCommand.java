/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
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

/*
   @Author: Ronan
*/
package org.gms.client.command.commands.gm2;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * GM2命令：设置所有背包格子数
 */
public class SetSlotCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("SetSlotCommand.message1"));
    }

    /**
     * 执行命令逻辑
     * @param c 客户端会话
     * @param params 命令参数
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        if (params.length < 1) {
            player.yellowMessage(I18nUtil.getMessage("SetSlotCommand.message2"));
            return;
        }

        int slots = (Integer.parseInt(params[0]) / 4) * 4;
        for (int i = 1; i < 5; i++) {
            int curSlots = player.getSlots(i);
            if (slots <= -curSlots) {
                continue;
            }

            player.gainSlots(i, slots - curSlots, true);
        }

        player.yellowMessage(I18nUtil.getMessage("SetSlotCommand.message3"));
    }
}
