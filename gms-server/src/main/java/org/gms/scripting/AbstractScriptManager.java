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
package org.gms.scripting;

import org.gms.client.Client;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 脚本管理器抽象基类，封装 GraalJS 引擎的创建、加载与缓存逻辑。
 * <p>
 * 子类（NPC、事件、传送门等）通过 {@link #getInvocableScriptEngine(String)} 获取可执行脚本引擎，
 * 并依赖本类实现脚本目录的国际化回退（{@code scripts-语言/} → {@code scripts/}）。
 * </p>
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractScriptManager.class);
    private static final String SCRIPT_DIRECTORY = "scripts";
    private final ScriptEngineFactory sef;

    /**
     * 初始化 GraalJS 脚本引擎工厂。
     */
    protected AbstractScriptManager() {
        sef = new ScriptEngineManager().getEngineByName("graal.js").getFactory();
    }

    /**
     * 按相对路径加载并执行脚本，返回 GraalJS 引擎实例。
     * <p>
     * 优先从语言目录 {@code scripts-语言/} 加载，缺失时回退到默认 {@code scripts/} 目录。
     * 加载或执行失败时返回 {@code null}。
     * </p>
     *
     * @param path 相对于脚本根目录的路径，例如 {@code "npc/1002000.js"}
     * @return 已执行脚本的 GraalJS 引擎，脚本不存在或执行失败时为 {@code null}
     */
    protected ScriptEngine getInvocableScriptEngine(String path) {
        // 读取当前服务端语言配置，用于拼出 scripts-语言 目录名，例如 scripts-zh-CN。
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);

        // 默认脚本目录始终是 scripts，里面保留英文原版脚本。
        Path scriptPath = Path.of(SCRIPT_DIRECTORY, path);
        // 语言脚本目录只放已本地化的脚本文件，不要求复制完整 scripts 目录。
        Path scriptLangPath = Path.of(SCRIPT_DIRECTORY + "-" + serviceProperty.getLanguage(), path);

        // 按文件级别选择脚本：先找语言文件，找不到再回退到英文原版文件。
        Path actualPath;
        if (Files.exists(scriptLangPath)) {
            actualPath = scriptLangPath;
        } else if (Files.exists(scriptPath)) {
            actualPath = scriptPath;
        } else {
            return null;
        }

        // 为本次实际命中的脚本文件创建独立 JS 引擎。
        ScriptEngine engine = sef.getScriptEngine();
        if (!(engine instanceof GraalJSScriptEngine graalScriptEngine)) {
            throw new IllegalStateException(I18nUtil.getExceptionMessage("AbstractScriptManager.getInvocableScriptEngine.exception1"));
        }

        // 开启脚本访问 Java 类的能力，保持现有脚本里的 Java.type 调用可用。
        enableScriptHostAccess(graalScriptEngine);

        // 用 UTF-8 读取并执行脚本；执行失败时返回 null，让调用方按原逻辑处理缺失脚本。
        try (BufferedReader br = Files.newBufferedReader(actualPath, StandardCharsets.UTF_8)) {
            engine.eval(br);
        } catch (final ScriptException | IOException t) {
            log.warn(I18nUtil.getLogMessage("AbstractScriptManager.getInvocableScriptEngine.warn1"), path, t);
            return null;
        }

        return graalScriptEngine;
    }

    /**
     * 按客户端缓存加载脚本引擎，同一客户端重复请求同一脚本时复用已加载的引擎。
     *
     * @param path 相对于脚本根目录的路径
     * @param c    当前客户端连接，用于读写脚本引擎缓存
     * @return 已执行脚本的 GraalJS 引擎
     */
    protected ScriptEngine getInvocableScriptEngine(String path, Client c) {
        // 缓存键统一使用默认脚本前缀加相对路径，避免读取和写入缓存时使用不同 key。
        String scriptKey = SCRIPT_DIRECTORY + "/" + path;
        ScriptEngine engine = c.getScriptEngine(scriptKey);
        if (engine == null) {
            // 客户端当前没有缓存时，再按文件级 i18n 规则加载脚本。
            engine = getInvocableScriptEngine(path);
            c.setScriptEngine(scriptKey, engine);
        }

        return engine;
    }

    /**
     * 允许脚本通过 Java.type() 查找并调用服务端 Java 类。
     */
    private void enableScriptHostAccess(GraalJSScriptEngine engine) {
        // GraalJS 的 host 访问开关需要写入引擎作用域绑定。
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowHostClassLookup", true);
    }

    /**
     * 清除指定脚本在客户端上的引擎缓存，用于脚本结束或重载后释放上下文。
     *
     * @param path 相对于脚本根目录的路径
     * @param c    当前客户端连接
     */
    protected void resetContext(String path, Client c) {
        // 重置时使用同一个缓存 key，确保能清掉上面 setScriptEngine 写入的脚本引擎。
        c.removeScriptEngine(SCRIPT_DIRECTORY + "/" + path);
    }
}
