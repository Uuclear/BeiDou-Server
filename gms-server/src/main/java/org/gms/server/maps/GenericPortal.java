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

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import org.gms.scripting.portal.PortalScriptManager;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通用传送门实现，支持脚本触发与目标地图跳转。
 */
public class GenericPortal implements Portal {
    private String name;
    private String target;
    private Point position;
    private int targetmap;
    private final int type;
    private boolean status = true;
    private int id;
    private String scriptName;
    private boolean portalState;
    private Lock scriptLock = null;

    /**
     * 构造 GenericPortal 实例。
     * @param type 类型
     */
    public GenericPortal(int type) {
        this.type = type;
    }

    /**
     * 获取ID。
     * @return int 类型结果
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * 设置ID。
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return position;
    }

    /**
     * 获取Target。
     * @return String 类型结果
     */
    @Override
    public String getTarget() {
        return target;
    }

    /**
     * 设置传送门状态。
     * @param newStatus newStatus
     */
    @Override
    public void setPortalStatus(boolean newStatus) {
        this.status = newStatus;
    }

    /**
     * 获取传送门状态。
     * @return boolean 类型结果
     */
    @Override
    public boolean getPortalStatus() {
        return status;
    }

    /**
     * 获取Target、地图、ID。
     * @return int 类型结果
     */
    @Override
    public int getTargetMapId() {
        return targetmap;
    }

    /**
     * 获取类型。
     * @return int 类型结果
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * 获取脚本名称。
     * @return String 类型结果
     */
    @Override
    public String getScriptName() {
        return scriptName;
    }

    /**
     * 设置名称。
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置位置。
     * @param position 坐标
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 设置Target。
     * @param target target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 设置Target、地图、ID。
     * @param targetmapid targetmapid
     */
    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    /**
     * 设置脚本名称。
     * @param scriptName scriptName
     */
    @Override
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;

        if (scriptName != null) {
            if (scriptLock == null) {
                scriptLock = new ReentrantLock(true);
            }
        } else {
            scriptLock = null;
        }
    }

    /**
     * 进入传送门。
     * @param c c
     */
    @Override
    public void enterPortal(Client c) {
        boolean changed = false;
        if (getScriptName() != null) {
            try {
                scriptLock.lock();
                try {
                    changed = PortalScriptManager.getInstance().executePortalScript(this, c);
                } finally {
                    scriptLock.unlock();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else if (getTargetMapId() != MapId.NONE) {
            Character chr = c.getPlayer();
            if (!(chr.getChalkboard() != null && GameConstants.isFreeMarketRoom(getTargetMapId()))) {
                MapleMap to = chr.getEventInstance() == null ? c.getChannelServer().getMapFactory().getMap(getTargetMapId()) : chr.getEventInstance().getMapInstance(getTargetMapId());
                Portal pto = to.getPortal(getTarget());
                if (pto == null) {// fallback for missing portals - no real life case anymore - interesting for not implemented areas
                    pto = to.getPortal(0);
                }
                chr.changeMap(to, pto); //late resolving makes this harder but prevents us from loading the whole world at once
                changed = true;
            } else {
                chr.dropMessage(5, "You cannot enter this map with the chalkboard opened.");
            }
        }
        if (!changed) {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    /**
     * 设置传送门状态。
     * @param state 状态值
     */
    @Override
    public void setPortalState(boolean state) {
        this.portalState = state;
    }

    /**
     * 获取传送门状态。
     * @return boolean 类型结果
     */
    @Override
    public boolean getPortalState() {
        return portalState;
    }
}
