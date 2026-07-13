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
package org.gms.scripting.npc;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.game.NextLevelType;
import org.gms.model.pojo.NextLevelContext;
import org.gms.net.server.world.PartyCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.ItemInformationProvider.ScriptedItem;
import org.gms.util.PacketCreator;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NPC脚本管理器，负责加载、管理和执行NPC对话JavaScript脚本。
 * 处理NPC对话的启动、玩家响应(action)、多级对话(nextLevel)流程控制和资源释放，
 * 同时也支持物品脚本的执行，是脚本系统的核心管理器之一。
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(NPCScriptManager.class);
    
    /**
     * 单例实例
     */
    private static final NPCScriptManager instance = new NPCScriptManager();

    /**
     * 客户端与NPC对话管理器的映射，存储当前正在进行NPC对话的客户端
     */
    private final Map<Client, NPCConversationManager> cms = new HashMap<>();
    
    /**
     * 客户端与脚本引擎的映射，缓存每个客户端的NPC脚本引擎
     */
    private final Map<Client, Invocable> scripts = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return NPCScriptManager单例对象
     */
    public static NPCScriptManager getInstance() {
        return instance;
    }

    /**
     * 检查指定的NPC脚本文件是否存在
     *
     * @param c 客户端连接对象
     * @param fileName 脚本文件名（不含路径和扩展名）
     * @return 如果脚本存在返回true，否则返回false
     */
    public boolean isNpcScriptAvailable(Client c, String fileName) {
        ScriptEngine engine = null;
        if (fileName != null) {
            engine = getInvocableScriptEngine("npc/" + fileName + ".js", c);
        }

        return engine != null;
    }

    /**
     * 启动NPC对话（使用NPC ID作为脚本名）
     *
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param chr 玩家角色对象
     * @return 对话成功启动返回true
     */
    public boolean start(Client c, int npc, Character chr) {
        return start(c, npc, -1, chr);
    }

    /**
     * 启动NPC对话（使用NPC ID和对象ID）
     *
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param oid NPC对象ID
     * @param chr 玩家角色对象
     * @return 对话成功启动返回true
     */
    public boolean start(Client c, int npc, int oid, Character chr) {
        return start(c, npc, oid, null, chr);
    }

    /**
     * 启动NPC对话（指定脚本文件名）
     *
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param fileName 脚本文件名
     * @param chr 玩家角色对象
     * @return 对话成功启动返回true
     */
    public boolean start(Client c, int npc, String fileName, Character chr) {
        return start(c, npc, -1, fileName, chr);
    }

    /**
     * 启动NPC对话（完整参数版本）
     *
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param oid NPC对象ID
     * @param fileName 脚本文件名
     * @param chr 玩家角色对象
     * @return 对话成功启动返回true
     */
    public boolean start(Client c, int npc, int oid, String fileName, Character chr) {
        return start(c, npc, oid, fileName, chr, false, "cm");
    }

    /**
     * 启动物品脚本
     *
     * @param c 客户端连接对象
     * @param scriptItem 脚本物品信息
     * @param chr 玩家角色对象
     * @return 脚本成功启动返回true
     */
    public boolean start(Client c, ScriptedItem scriptItem, Character chr) {
        return start(c, scriptItem.getNpc(), -1, scriptItem.getScript(), chr, true, "im");
    }

    /**
     * 启动组队NPC对话（用于组队任务等）
     *
     * @param filename 脚本文件名
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param chrs 组队成员列表
     */
    public void start(String filename, Client c, int npc, List<PartyCharacter> chrs) {
        try {
            final NPCConversationManager cm = new NPCConversationManager(c, npc, chrs, true);
            cm.dispose();
            if (cms.containsKey(c)) {
                return;
            }
            cms.put(c, cm);
            ScriptEngine engine = getInvocableScriptEngine("npc/" + filename + ".js", c);

            if (engine == null) {
                c.getPlayer().dropMessage(1, "NPC " + npc + " is uncoded.");
                cm.dispose();
                return;
            }
            engine.put("cm", cm);

            Invocable invocable = (Invocable) engine;
            scripts.put(c, invocable);
            try {
                invocable.invokeFunction("start", chrs);
            } catch (final NoSuchMethodException nsme) {
                nsme.printStackTrace();
            }

        } catch (final Exception e) {
            log.error("Error starting NPC script: {}", npc, e);
            dispose(c);
        }
    }

    /**
     * 启动NPC/物品对话的核心实现方法
     *
     * @param c 客户端连接对象
     * @param npc NPC ID
     * @param oid NPC对象ID
     * @param fileName 脚本文件名
     * @param chr 玩家角色对象
     * @param itemScript 是否为物品脚本
     * @param engineName 脚本中对话管理器变量名（cm或im）
     * @return 对话成功启动返回true
     */
    private boolean start(Client c, int npc, int oid, String fileName, Character chr, boolean itemScript, String engineName) {
        try {
            final NPCConversationManager cm = new NPCConversationManager(c, npc, oid, fileName, itemScript);
            if (cms.containsKey(c)) {
                dispose(c);
            }
            if (c.canClickNPC()) {
                cms.put(c, cm);
                ScriptEngine engine = null;
                if (!itemScript) {
                    if (fileName != null) {
                        engine = getInvocableScriptEngine("npc/" + fileName + ".js", c);
                        if (engine == null) {
                            engine = getInvocableScriptEngine("BeiDouSpecial/" + fileName + ".js", c);
                        }
                    }
                } else {
                    if (fileName != null) {
                        engine = getInvocableScriptEngine("item/" + fileName + ".js", c);
                    }
                }
                if (engine == null) {
                    engine = getInvocableScriptEngine("npc/" + npc + ".js", c);
                    cm.resetItemScript();
                }

                if (engine == null) {
                    dispose(c);
                    return false;
                }
                engine.put(engineName, cm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (final NoSuchMethodException nsme) {
                    try {
                        iv.invokeFunction("start", chr);
                    } catch (final NoSuchMethodException nsma) {
                        nsma.printStackTrace();
                    }
                }
            } else {
                c.sendPacket(PacketCreator.enableActions());
            }
            return true;
        } catch (Exception e) {
            log.error("Error starting NPC script: {}", npc, e);
            dispose(c, true);

            return false;
        }
    }

    /**
     * 处理玩家在NPC对话中的普通响应
     *
     * @param c 客户端连接对象
     * @param mode 响应模式（0=否/后退, 1=是/继续）
     * @param type 对话类型
     * @param selection 玩家选择的选项
     */
    public void action(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();
                c.setClickedNPC();
                iv.invokeFunction("action", mode, type, selection);
            } catch (Exception t) {
                if (getCM(c) != null) {
                    log.error("Error performing NPC script action for npc: {}", getCM(c).getNpc(), t);
                }
                dispose(c, true);
            } finally {
                c.releaseClient();
            }
        }
    }

    /**
     * 处理多级对话流程的下一级响应，支持多种对话类型（选择、输入数字、输入文本等）
     *
     * @param c 客户端连接对象
     * @param mode 响应模式
     * @param type 对话类型
     * @param selection 玩家选择/输入
     */
    public void nextLevel(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();
                c.setClickedNPC();
                NextLevelContext nextLevelContext = c.getCM().getNextLevelContext();
                switch (nextLevelContext.getLevelType()) {
                    case NextLevelType.SEND_SELECT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getPrefix() + selection);
                    }
                    case NextLevelType.GET_INPUT_NUMBER, NextLevelType.SEND_NEXT_SELECT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), selection);
                    }
                    case NextLevelType.GET_INPUT_TEXT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), c.getCM().getText());
                    }
                    case NextLevelType.SEND_LAST_NEXT, NextLevelType.SEND_NEXT, NextLevelType.SEND_LAST,
                         NextLevelType.SEND_OK, NextLevelType.SEND_ACCEPT_DECLINE, NextLevelType.SEND_YES_NO -> {
                        if (mode == -1) {
                            dispose(c, true);
                            return;
                        }
                        if (mode == 0) {
                            iv.invokeFunction("level" + nextLevelContext.getLastLevel());
                        } else {
                            iv.invokeFunction("level" + nextLevelContext.getNextLevel());
                        }
                    }
                    default -> {
                        log.error("Unsupported level type: {}", nextLevelContext.getLevelType());
                        dispose(c, true);
                    }
                }
            } catch (Exception t) {
                if (getCM(c) != null) {
                    log.error("Error performing NPC script action for npc: {}", getCM(c).getNpc(), t);
                }
                dispose(c, true);
            } finally {
                c.releaseClient();
            }
        }
    }

    /**
     * 释放NPC对话资源
     *
     * @param cm NPC对话管理器
     */
    public void dispose(NPCConversationManager cm) {
        Client c = cm.getClient();
        c.getPlayer().setCS(false);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        cms.remove(c);
        scripts.remove(c);

        String scriptFolder = (cm.isItemScript() ? "item" : "npc");
        if (cm.getScriptName() != null) {
            resetContext(scriptFolder + "/" + cm.getScriptName() + ".js", c);
        } else {
            resetContext(scriptFolder + "/" + cm.getNpc() + ".js", c);
        }

        c.getPlayer().flushDelayedUpdateQuests();
    }

    /**
     * 释放指定客户端的NPC对话资源
     *
     * @param c 客户端连接对象
     */
    public void dispose(Client c) {
        dispose(c, false);
    }

    /**
     * 释放指定客户端的NPC对话资源（可选择是否重新启用玩家操作）
     *
     * @param c 客户端连接对象
     * @param action 是否发送启用操作数据包
     */
    public void dispose(Client c, boolean action) {
        NPCConversationManager cm = cms.get(c);
        if (cm != null) {
            dispose(cm);
        }
        if (action) {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    /**
     * 获取指定客户端的NPC对话管理器
     *
     * @param c 客户端连接对象
     * @return 对应的NPCConversationManager，如果不存在返回null
     */
    public NPCConversationManager getCM(Client c) {
        return cms.get(c);
    }

}
