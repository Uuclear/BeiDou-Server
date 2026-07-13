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

import org.gms.client.Character;
import org.gms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图脚本管理器，负责加载、缓存和执行地图JavaScript脚本。
 * 处理玩家进入地图时触发的脚本逻辑，支持脚本缓存和热重载，
 * 可配置是否为首次进入地图的用户执行脚本。
 *
 * @author OdinMS Team
 */
public class MapScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(MapScriptManager.class);
    
    /**
     * 单例实例
     */
    private static final MapScriptManager instance = new MapScriptManager();

    /**
     * 已加载的地图脚本缓存，key为脚本路径，value为可调用的脚本引擎
     */
    private final Map<String, Invocable> scripts = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return MapScriptManager单例对象
     */
    public static MapScriptManager getInstance() {
        return instance;
    }

    /**
     * 重新加载所有地图脚本，清空脚本缓存
     */
    public void reloadScripts() {
        scripts.clear();
    }

    /**
     * 执行地图脚本
     *
     * @param c 客户端连接对象
     * @param mapScriptPath 地图脚本路径
     * @param firstUser 是否只对首次进入该地图的玩家执行（防止重复执行）
     * @return 脚本成功执行返回true，否则返回false
     */
    public boolean runMapScript(Client c, String mapScriptPath, boolean firstUser) {
        if (firstUser) {
            Character chr = c.getPlayer();
            int mapid = chr.getMapId();
            if (chr.hasEntered(mapScriptPath, mapid)) {
                return false;
            } else {
                chr.enteredScript(mapScriptPath, mapid);
            }
        }

        Invocable iv = scripts.get(mapScriptPath);
        if (iv != null) {
            try {
                iv.invokeFunction("start", new MapScriptMethods(c));
                return true;
            } catch (final ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            iv = (Invocable) getInvocableScriptEngine("map/" + mapScriptPath + ".js");
            if (iv == null) {
                return false;
            }

            scripts.put(mapScriptPath, iv);
            iv.invokeFunction("start", new MapScriptMethods(c));
            return true;
        } catch (final Exception e) {
            log.error("Error running map script {}", mapScriptPath, e);
        }

        return false;
    }
}