package org.gms.client.command.commands.gm6;

import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * GM6（最高权限）命令：运行devtest.js脚本，在不重启服务的情况下测试某些内容
 */
public class DevtestCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("DevtestCommand.message1"));
    }

    private static final Logger log = LoggerFactory.getLogger(DevtestCommand.class);

    private static class DevtestScriptManager extends AbstractScriptManager {

        /**
         * 获取Invocable脚本Engine
         * @param path path
         * @return 返回值
         */
        @Override
        public ScriptEngine getInvocableScriptEngine(String path) {
            return super.getInvocableScriptEngine(path);
        }

    }

    /**
     * 执行命令逻辑
     * @param client 客户端会话
     * @param params 命令参数
     */
    @Override
    public void execute(Client client, String[] params) {
        DevtestScriptManager scriptManager = new DevtestScriptManager();
        ScriptEngine scriptEngine = scriptManager.getInvocableScriptEngine("devtest.js");
        try {
            Invocable invocable = (Invocable) scriptEngine;
            invocable.invokeFunction("run", client.getPlayer());
        } catch (ScriptException | NoSuchMethodException e) {
            log.info(I18nUtil.getMessage("DevtestCommand.message2"), e);
        }
    }
}
