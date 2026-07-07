package org.gms.client.command.commands.gm0;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.util.I18nUtil;

/**
 * GM0（所有玩家可用）命令：展示全部点数
 */
public class ReadPointsCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("ReadPointsCommand.message1"));
    }

    /**
     * 执行命令逻辑
     * @param client 客户端会话
     * @param params 命令参数
     */
    @Override
    public void execute(Client client, String[] params) {

        Character player = client.getPlayer();
        if (params.length > 2) {
            player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message2"));
            return;
        } else if (params.length == 0) {
            player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints() + " | "
                    + I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
            return;
        }

        switch (params[0]) {
            case "rp":
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints());
                break;
            case "vp":
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
                break;
            default:
                player.yellowMessage(I18nUtil.getMessage("ReadPointsCommand.message3") + player.getRewardPoints() + " | "
                        + I18nUtil.getMessage("ReadPointsCommand.message4") + player.getClient().getVotePoints());
                break;
        }
    }
}