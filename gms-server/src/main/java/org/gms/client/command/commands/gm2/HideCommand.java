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
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command.commands.gm2;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * GM2命令：隐身
 */
public class HideCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("HideCommand.message1"));
    }

    /**
     * 执行命令逻辑
     * @param c 客户端会话
     * @param params 命令参数
     */
    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();
        SkillFactory.getSkill(9101004).getEffect(SkillFactory.getSkill(9101004).getMaxLevel()).applyTo(player);

    }
}
