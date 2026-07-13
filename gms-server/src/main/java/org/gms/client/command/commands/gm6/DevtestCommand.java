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
 * Devtest命令类 - 游戏逻辑模块的核心实现
 *
 * @author GMS Server
 */
public class DevtestCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("DevtestCommand.message1"));
    }

    private static final Logger log = LoggerFactory.getLogger(DevtestCommand.class);

    private static class DevtestScriptManager extends AbstractScriptManager {

        @Override
        public ScriptEngine getInvocableScriptEngine(String path) {
            return super.getInvocableScriptEngine(path);
        }

    }

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
