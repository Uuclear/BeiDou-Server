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
 * 传送门脚本玩家交互 API，注入为 {@link PortalScript#enter} 的参数 {@code ppi}。
 * <p>
 * 在 {@link AbstractPlayerInteraction} 基础上提供当前传送门对象、关联地图脚本触发、
 * 门户封锁及账号角色查询等传送门专用能力。
 * </p>
 */
public class PortalPlayerInteraction extends AbstractPlayerInteraction {
    private final Portal portal;

    public PortalPlayerInteraction(Client c, Portal portal) {
        super(c);
        this.portal = portal;
    }

/** 获取Portal */
    public Portal getPortal() {
        return portal;
    }

/** 执行地图脚本 */
    public void runMapScript() {
        MapScriptManager msm = MapScriptManager.getInstance();
        msm.runMapScript(c, "onUserEnter/" + portal.getScriptName(), false);
    }

/** 账号下是否存在 30 级以上角色 */
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

/** 临时封锁当前传送门 */
    public void blockPortal() {
        c.getPlayer().blockPortal(getPortal().getScriptName());
    }

/** 解除传送门封锁 */
    public void unblockPortal() {
        c.getPlayer().unblockPortal(getPortal().getScriptName());
    }

/** 播放传送门音效 */
    public void playPortalSound() {
        c.sendPacket(PacketCreator.playPortalSound());
    }
}