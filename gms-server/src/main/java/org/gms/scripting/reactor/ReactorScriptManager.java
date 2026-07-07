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
package org.gms.scripting.reactor;

import org.gms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.maps.Reactor;
import org.gms.server.maps.ReactorDropEntry;
import org.gms.util.DatabaseConnection;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 反应堆脚本管理器（单例），按反应堆 ID 加载 {@code scripts/reactor/{id}.js}。
 * <p>
 * 向引擎注入 {@code rm}（{@link ReactorActionManager}），在受击、激活、接触等时机
 * 调用脚本可选函数 {@code hit}、{@code act}、{@code touch}、{@code untouch}。
 * 同时缓存数据库中的反应堆掉落表。
 * </p>
 *
 * @author Lerk
 */
public class ReactorScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(ReactorScriptManager.class);
    private static final ReactorScriptManager instance = new ReactorScriptManager();

    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

/** 获取单例实例 */
    public static ReactorScriptManager getInstance() {
        return instance;
    }

/** 反应堆被攻击时调用脚本 hit 函数 */
    public void onHit(Client c, Reactor reactor) {
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction("hit");
        } catch (final NoSuchMethodException e) {
            //do nothing, hit is OPTIONAL
        } catch (final ScriptException | NullPointerException e) {
            log.error("Error during onHit script for reactor: {}", reactor.getId(), e);
        }
    }

/** 反应堆被激活时调用脚本 act 函数 */
    public void act(Client c, Reactor reactor) {
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction("act");
        } catch (final ScriptException | NoSuchMethodException | NullPointerException e) {
            log.error("Error during act script for reactor: {}", reactor.getId(), e);
        }
    }

/** 获取反应堆掉落配置（数据库缓存） */
    public List<ReactorDropEntry> getDrops(int reactorId) {
        List<ReactorDropEntry> ret = drops.get(reactorId);
        if (ret == null) {
            ret = new LinkedList<>();
            try (Connection con = DatabaseConnection.getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT itemid, chance, questid FROM reactordrops WHERE reactorid = ? AND chance >= 0")) {
                    ps.setInt(1, reactorId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            ret.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("Error getting drops for reactor: {}", reactorId);
            }
            drops.put(reactorId, ret);
        }
        return ret;
    }

/** 清空反应堆掉落缓存 */
    public void clearDrops() {
        drops.clear();
    }

/** 玩家接触反应堆时调用 touch 函数 */
    public void touch(Client c, Reactor reactor) {
        touching(c, reactor, true);
    }

/** 玩家离开反应堆时调用 untouch 函数 */
    public void untouch(Client c, Reactor reactor) {
        touching(c, reactor, false);
    }

    private void touching(Client c, Reactor reactor, boolean touching) {
        final String functionName = touching ? "touch" : "untouch";
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction(functionName);
        } catch (final ScriptException | NoSuchMethodException | NullPointerException e) {
            log.error("Error during {} script for reactor: {}", functionName, reactor.getId(), e);
        }
    }

    private Invocable initializeInvocable(Client c, Reactor reactor) {
        ScriptEngine engine = getInvocableScriptEngine("reactor/" + reactor.getId() + ".js", c);
        if (engine == null) {
            return null;
        }

        Invocable iv = (Invocable) engine;
        // 向 GraalJS 注入 rm，脚本内通过 rm 调用 ReactorActionManager API
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
        engine.put("rm", rm);

        return iv;
    }
}