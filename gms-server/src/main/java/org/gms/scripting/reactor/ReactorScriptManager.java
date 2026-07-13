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
 * 反应堆脚本管理器，负责加载、缓存和执行反应堆JavaScript脚本。
 * 处理反应堆的击打(hit)、触发(act)、触碰(touch/untouch)等事件，
 * 管理反应堆掉落物品配置，支持单例模式。
 *
 * @author Lerk
 */
public class ReactorScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(ReactorScriptManager.class);
    
    /**
     * 单例实例
     */
    private static final ReactorScriptManager instance = new ReactorScriptManager();

    /**
     * 反应堆掉落配置缓存，key为反应堆ID，value为掉落条目列表
     */
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return ReactorScriptManager单例对象
     */
    public static ReactorScriptManager getInstance() {
        return instance;
    }

    /**
     * 反应堆被击打时调用，执行脚本中的hit函数。
     * hit函数是可选的，如果脚本中未定义则不执行任何操作。
     *
     * @param c 客户端连接对象
     * @param reactor 被击打的反应堆对象
     */
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

    /**
     * 反应堆被触发（操作完成）时调用，执行脚本中的act函数。
     *
     * @param c 客户端连接对象
     * @param reactor 被触发的反应堆对象
     */
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

    /**
     * 获取指定反应堆的掉落配置列表。
     * 如果缓存中没有则从数据库加载并缓存。
     *
     * @param reactorId 反应堆ID
     * @return 反应堆掉落条目列表
     */
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

    /**
     * 清空所有反应堆掉落配置缓存
     */
    public void clearDrops() {
        drops.clear();
    }

    /**
     * 玩家触碰反应堆时调用，执行脚本中的touch函数。
     *
     * @param c 客户端连接对象
     * @param reactor 被触碰的反应堆对象
     */
    public void touch(Client c, Reactor reactor) {
        touching(c, reactor, true);
    }

    /**
     * 玩家离开反应堆时调用，执行脚本中的untouch函数。
     *
     * @param c 客户端连接对象
     * @param reactor 被离开的反应堆对象
     */
    public void untouch(Client c, Reactor reactor) {
        touching(c, reactor, false);
    }

    /**
     * 处理触碰/离开事件的通用方法。
     *
     * @param c 客户端连接对象
     * @param reactor 反应堆对象
     * @param touching true表示触碰(touch)，false表示离开(untouch)
     */
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

    /**
     * 初始化反应堆脚本引擎，创建ReactorActionManager并注入到脚本上下文中。
     *
     * @param c 客户端连接对象
     * @param reactor 反应堆对象
     * @return 可调用的脚本引擎接口，如果脚本不存在返回null
     */
    private Invocable initializeInvocable(Client c, Reactor reactor) {
        ScriptEngine engine = getInvocableScriptEngine("reactor/" + reactor.getId() + ".js", c);
        if (engine == null) {
            return null;
        }

        Invocable iv = (Invocable) engine;
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
        engine.put("rm", rm);

        return iv;
    }
}