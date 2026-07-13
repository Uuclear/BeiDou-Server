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
package org.gms.scripting.map;

import org.gms.client.Client;
import org.gms.client.QuestStatus;
import org.gms.constants.game.DelayedQuestUpdate;
import org.gms.constants.id.MapId;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.server.quest.Quest;
import org.gms.util.PacketCreator;

/**
 * 地图脚本方法类，继承自AbstractPlayerInteraction，
 * 为地图脚本提供与玩家交互的专用方法。包含职业开场动画播放、
 * 探险家勋章任务进度追踪、特殊地图事件处理等功能。
 *
 * @author OdinMS Team
 */
public class MapScriptMethods extends AbstractPlayerInteraction {

    /**
     * 勋章完成提示字符串
     */
    private final String rewardstring = " 勋章挑战已完成！请找勋章老人领取你的勋章。";

    /**
     * 构造地图脚本方法对象
     *
     * @param c 客户端连接对象
     */
    public MapScriptMethods(Client c) {
        super(c);
    }

    /**
     * 显示骑士团职业开场介绍动画，根据当前地图显示对应场景
     */
    public void displayCygnusIntro() {
        switch (c.getPlayer().getMapId()) {
            case MapId.CYGNUS_INTRO_LEAD -> {
                lockUI();
                c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene0"));
            }
            case MapId.CYGNUS_INTRO_WARRIOR -> c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene1"));
            case MapId.CYGNUS_INTRO_BOWMAN -> c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene2"));
            case MapId.CYGNUS_INTRO_MAGE -> c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene3"));
            case MapId.CYGNUS_INTRO_PIRATE -> c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene4"));
            case MapId.CYGNUS_INTRO_THIEF -> c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene5"));
            case MapId.CYGNUS_INTRO_CONCLUSION -> {
                lockUI();
                c.sendPacket(PacketCreator.showIntro("Effect/Direction.img/cygnusJobTutorial/Scene6"));
            }
        }
    }

    /**
     * 显示战神(Aran)职业开场介绍动画，根据玩家性别显示不同版本
     */
    public void displayAranIntro() {
        switch (c.getPlayer().getMapId()) {
            case MapId.ARAN_TUTO_1 -> {
                lockUI();
                c.sendPacket(PacketCreator.showIntro("Effect/Direction1.img/aranTutorial/Scene0"));
            }
            case MapId.ARAN_TUTO_2 -> c.sendPacket(PacketCreator.showIntro("Effect/Direction1.img/aranTutorial/Scene1" + c.getPlayer().getGender()));
            case MapId.ARAN_TUTO_3 -> c.sendPacket(PacketCreator.showIntro("Effect/Direction1.img/aranTutorial/Scene2" + c.getPlayer().getGender()));
            case MapId.ARAN_TUTO_4 -> c.sendPacket(PacketCreator.showIntro("Effect/Direction1.img/aranTutorial/Scene3"));
            case MapId.ARAN_POLEARM -> {
                lockUI();
                c.sendPacket(PacketCreator.showIntro("Effect/Direction1.img/aranTutorial/HandedPoleArm" + c.getPlayer().getGender()));
            }
        }
    }

    /**
     * 开始冒险家职业体验介绍动画，根据玩家职业显示对应场景
     */
    public void startExplorerExperience() {
        switch (c.getPlayer().getMapId()) {
        case 1020100:
            c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/swordman/Scene" + c.getPlayer().getGender()));
            break;
        case 1020200:
            c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/magician/Scene" + c.getPlayer().getGender()));
            break;
        case 1020300:
            c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/archer/Scene" + c.getPlayer().getGender()));
            break;
        case 1020400:
            c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/rogue/Scene" + c.getPlayer().getGender()));
            break;
        case 1020500:
            c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/pirate/Scene" + c.getPlayer().getGender()));
            break;
        }
    }

    /**
     * 显示"开始冒险"引导动画
     */
    public void goAdventure() {
        lockUI();
        c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/goAdventure/Scene" + c.getPlayer().getGender()));
    }

    /**
     * 显示前往明珠港的引导动画
     */
    public void goLith() {
        lockUI();
        c.sendPacket(PacketCreator.showIntro("Effect/Direction3.img/goLith/Scene" + c.getPlayer().getGender()));
    }

    /**
     * 处理探险家勋章任务进度，当玩家探索新区域时更新勋章进度。
     *
     * @param questid 勋章任务ID
     * @param questName 勋章名称
     */
    public void explorerQuest(short questid, String questName) {
        Quest quest = Quest.getInstance(questid);
        if (isQuestCompleted(questid)) {
            return;
        }
        
        if (!isQuestStarted(questid)) {
            if (!quest.forceStart(getPlayer(), 9000066)) {
                return;
            }
        }
        QuestStatus qs = getPlayer().getQuest(quest);
        if (!qs.addMedalMap(getPlayer().getMapId())) {
            return;
        }
        String status = Integer.toString(qs.getMedalProgress());
        String infoex = qs.getInfoEx(0);

        getPlayer().setQuestProgress(quest.getId(), (int)quest.getInfoNumber(qs.getStatus()), status);

        StringBuilder smp = new StringBuilder();
        StringBuilder etm = new StringBuilder();
        if (status.equals(infoex)) {
            etm.append("获得 ").append(questName).append(" 勋章！");
            smp.append("你获得了 <").append(questName).append(">").append(rewardstring);
            getPlayer().sendPacket(PacketCreator.getShowQuestCompletion(quest.getId()));
        } else {
            getPlayer().sendPacket(PacketCreator.earnTitleMessage(status + "/" + infoex + " 区域已探索"));
            etm.append("正在挑战 ").append(questName).append(" 勋章");
            smp.append("你正在挑战 ").append(questName).append(" 勋章。 ").append(status).append("/").append(infoex);
        }
        getPlayer().sendPacket(PacketCreator.earnTitleMessage(etm.toString()));
        showInfoText(smp.toString());
    }

    /**
     * 处理"站在巅峰的人"勋章任务(29004)，需要探索5个高峰地图。
     */
    public void touchTheSky() {
        Quest quest = Quest.getInstance(29004);
        if (!isQuestStarted(29004)) {
            if (!quest.forceStart(getPlayer(), 9000066)) {
                return;
            }
        }
        QuestStatus qs = getPlayer().getQuest(quest);
        if (!qs.addMedalMap(getPlayer().getMapId())) {
            return;
        }
        String status = Integer.toString(qs.getMedalProgress());
        getPlayer().setQuestProgress(quest.getId(), (int)quest.getInfoNumber(qs.getStatus()), status);
        getPlayer().sendPacket(PacketCreator.earnTitleMessage(status + "/5 已完成"));
        getPlayer().sendPacket(PacketCreator.earnTitleMessage("站在巅峰的人 勋章挑战正在进行中"));
        if (Integer.toString(qs.getMedalProgress()).equals(qs.getInfoEx(0))) {
            showInfoText("站在巅峰的人" + rewardstring);
            getPlayer().sendPacket(PacketCreator.getShowQuestCompletion(quest.getId()));
        } else {
            showInfoText("站在巅峰的人 勋章挑战正在进行中。 " + status + "/5 已完成");
        }
    }
}
