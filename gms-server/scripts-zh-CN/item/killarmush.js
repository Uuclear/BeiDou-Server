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

/**
 * 脚本类型：物品
 * 对象 ID：2430014
 * 功能描述：蘑菇城任务道具，在地图 106020300 靠近 obstacle 传送门时使用，消除结界并完成任务 100202。
 */
function start(){
    if (im.getMapId() == 106020300) {
        var portal = im.getMap().getPortal("obstacle");
        if (portal != null && portal.getPosition().distance(im.getPlayer().getPosition()) < 240) {
            if (!(im.isQuestStarted(100202) || im.isQuestCompleted(100202))) {
                im.startQuest(100202);
            }
            im.removeAll(2430014);
            im.showInfo("Effect/OnUserEff/normalEffect/mushroomcastle/chatBalloon2");
            im.dropMessage(6,'好像有什么动静...嗯？是结界被消除了');
        } else {
            im.message('尽可能的接近魔法结界才能将其消除');
        }
    } else {
        im.message('这里似乎没有需要消除的魔法结界');
    }

    im.dispose();
}

