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
 * 地图脚本管理器（单例），执行 {@code scripts/map/} 下用户进入/离开等脚本。
 * <p>
 * 脚本需实现 {@code start(MapScriptMethods)} 入口；已加载脚本缓存在内存中。
 * {@code firstUser} 为 true 时同一地图仅首个进入玩家触发一次（记录于角色 enteredScript）。
 * </p>
 */
public class MapScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(MapScriptManager.class);
    private static final MapScriptManager instance = new MapScriptManager();

    private final Map<String, Invocable> scripts = new HashMap<>();

/** 获取单例实例 */
    public static MapScriptManager getInstance() {
        return instance;
    }

/** 清空地图脚本缓存 */
    public void reloadScripts() {
        scripts.clear();
    }

/** 执行地图脚本 */
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