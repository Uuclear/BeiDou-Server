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
package org.gms.scripting.portal;

import org.gms.client.Client;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.scripting.map.MapScriptManager;
import org.gms.server.maps.Portal;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 传送门玩家交互类，继承自AbstractPlayerInteraction，
 * 为传送门脚本提供与玩家和游戏世界交互的专用方法。
 * 包含传送门相关的特殊功能，如检查玩家等级、播放传送门音效、阻止/解除传送门等。
 *
 * @author OdinMS Team
 */
public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    /**
     * 当前交互关联的传送门对象
     */
    private final Portal portal;

    /**
     * 构造传送门玩家交互对象
     *
     * @param c 客户端连接对象
     * @param portal 当前传送门对象
     */
    public PortalPlayerInteraction(Client c, Portal portal) {
        super(c);
        this.portal = portal;
    }

    /**
     * 获取当前交互的传送门对象
     *
     * @return 当前传送门对象
     */
    public Portal getPortal() {
        return portal;
    }

    /**
     * 执行地图进入脚本，通常在玩家通过传送门进入新地图时触发
     */
    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(c, "onUserEnter/" + portal.getScriptName(), false);
    }

    /**
     * 检查该账户下是否存在等级达到30级的角色。
     * 用于某些需要玩家有一定游戏经验才能进入的传送门判断。
     *
     * @return 如果有30级以上角色返回true，否则返回false
     */
    public boolean hasLevel30Character() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `level` FROM `characters` WHERE accountid = ?")) {
            ps.setInt(1, getPlayer().getAccountId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("level") >= 30) {
                        return true;
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return getPlayer().getLevel() >= 30;
    }

    /**
     * 阻止玩家使用当前传送门
     */
    public void blockPortal() {
        c.getPlayer().blockPortal(getPortal().getScriptName());
    }

    /**
     * 解除传送门的阻止状态，允许玩家再次使用
     */
    public void unblockPortal() {
        c.getPlayer().unblockPortal(getPortal().getScriptName());
    }

    /**
     * 播放传送门使用音效
     */
    public void playPortalSound() {
        c.sendPacket(PacketCreator.playPortalSound());
    }
}