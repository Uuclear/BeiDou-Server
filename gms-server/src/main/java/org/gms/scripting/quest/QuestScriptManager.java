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
package org.gms.scripting.quest;

import org.gms.client.Client;
import org.gms.client.QuestStatus;
import org.gms.constants.game.GameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.quest.Quest;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务脚本管理器，负责加载、管理和执行任务JavaScript脚本。
 * 处理任务的开始、进行中对话、完成等各个阶段的脚本逻辑，
 * 支持勋章任务的通用脚本回退机制。
 *
 * @author RMZero213
 */
public class QuestScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(QuestScriptManager.class);
    
    /**
     * 单例实例
     */
    private static final QuestScriptManager instance = new QuestScriptManager();

    /**
     * 客户端与任务动作管理器的映射，存储当前正在进行任务对话的客户端
     */
    private final Map<Client, QuestActionManager> qms = new HashMap<>();
    
    /**
     * 客户端与脚本引擎的映射，缓存每个客户端的任务脚本引擎
     */
    private final Map<Client, Invocable> scripts = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return QuestScriptManager单例对象
     */
    public static QuestScriptManager getInstance() {
        return instance;
    }

    /**
     * 获取指定任务的脚本引擎。
     * 如果是勋章任务且找不到专属脚本，则回退到通用勋章任务脚本。
     *
     * @param c 客户端连接对象
     * @param questid 任务ID
     * @return 任务脚本引擎，如果找不到脚本返回null
     */
    private ScriptEngine getQuestScriptEngine(Client c, short questid) {
        ScriptEngine engine = getInvocableScriptEngine("quest/" + questid + ".js", c);
        if (engine == null && GameConstants.isMedalQuest(questid)) {
            engine = getInvocableScriptEngine("quest/medalQuest.js", c);
        }

        return engine;
    }

    /**
     * 开始任务对话，初始调用start函数。
     *
     * @param c 客户端连接对象
     * @param questid 任务ID
     * @param npc 交互的NPC ID
     */
    public void start(Client c, short questid, int npc) {
        Quest quest = Quest.getInstance(questid);
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, true);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    log.warn("START Quest {} is uncoded.", questid);
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0);
            }
        } catch (final Throwable t) {
            log.error("Error starting quest script: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 处理任务开始阶段的玩家响应（继续对话、选择选项等）。
     *
     * @param c 客户端连接对象
     * @param mode 对话模式（0=否, 1=是, 等）
     * @param type 对话类型
     * @param selection 玩家选择的选项
     */
    public void start(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.setClickedNPC();
                iv.invokeFunction("start", mode, type, selection);
            } catch (final Exception e) {
                log.error("Error starting quest script: {}", getQM(c).getQuest(), e);
                dispose(c);
            }
        }
    }

    /**
     * 完成任务对话，初始调用end函数。
     * 只有当任务处于开始状态且玩家在NPC附近（或任务自动完成）时才能执行。
     *
     * @param c 客户端连接对象
     * @param questid 任务ID
     * @param npc 交互的NPC ID
     */
    public void end(Client c, short questid, int npc) {
        Quest quest = Quest.getInstance(questid);
        if (!c.getPlayer().getQuest(quest).getStatus().equals(QuestStatus.Status.STARTED) || (!c.getPlayer().getMap().containsNPC(npc) && !quest.isAutoComplete())) {
            dispose(c);
            return;
        }
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, false);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    log.warn("END Quest {} is uncoded.", questid);
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0);
            }
        } catch (final Throwable t) {
            log.error("Error starting quest script: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 处理任务完成阶段的玩家响应。
     *
     * @param c 客户端连接对象
     * @param mode 对话模式
     * @param type 对话类型
     * @param selection 玩家选择的选项
     */
    public void end(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.setClickedNPC();
                iv.invokeFunction("end", mode, type, selection);
            } catch (final Exception e) {
                log.error("Error ending quest script: {}", getQM(c).getQuest(), e);
                dispose(c);
            }
        }
    }

    /**
     * 触发任务的raiseOpen事件，用于任务信息提示等。
     *
     * @param c 客户端连接对象
     * @param questid 任务ID
     * @param npc NPC ID
     */
    public void raiseOpen(Client c, short questid, int npc) {
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, true);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("raiseOpen");
            }
        } catch (final Throwable t) {
            log.error("Error during quest script raiseOpen for quest: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 释放指定客户端的任务脚本资源。
     *
     * @param qm 任务动作管理器
     * @param c 客户端连接对象
     */
    public void dispose(QuestActionManager qm, Client c) {
        qms.remove(c);
        scripts.remove(c);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        resetContext("quest/" + qm.getQuest() + ".js", c);
        c.getPlayer().flushDelayedUpdateQuests();
    }

    /**
     * 释放指定客户端的任务脚本资源（重载版本）。
     *
     * @param c 客户端连接对象
     */
    public void dispose(Client c) {
        QuestActionManager qm = qms.get(c);
        if (qm != null) {
            dispose(qm, c);
        }
    }

    /**
     * 获取指定客户端的任务动作管理器。
     *
     * @param c 客户端连接对象
     * @return 对应的QuestActionManager，如果不存在返回null
     */
    public QuestActionManager getQM(Client c) {
        return qms.get(c);
    }

    /**
     * 重新加载所有任务脚本，清空缓存。
     */
    public void reloadQuestScripts() {
        scripts.clear();
        qms.clear();
    }

    /**
     * 检查任务脚本中是否存在指定的函数。
     *
     * @param c 客户端连接对象
     * @param questid 任务ID
     * @param npc NPC ID
     * @param functionName 要检查的函数名
     * @return 如果函数存在返回true，否则返回false
     */
    public boolean checkFunctionExists(Client c, short questid, int npc, String functionName) {
        ScriptEngine engine = getQuestScriptEngine(c, questid);
        if (engine == null) {
            return false;
        }
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, false);
            engine.put("qm", qm);
            String script = "function checkFunction(funcName) { return typeof this[funcName] === 'function'; }";
            engine.eval(script);

            Invocable invocable = (Invocable) engine;
            boolean exists = (Boolean) invocable.invokeFunction("checkFunction", functionName);

            qm.dispose();
            return exists;
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            dispose(c);
        }
        return false;
    }


}
