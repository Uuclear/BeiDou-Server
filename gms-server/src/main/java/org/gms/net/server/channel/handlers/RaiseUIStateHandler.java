package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.QuestStatus;
import org.gms.constants.game.DelayedQuestUpdate;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.scripting.quest.QuestScriptManager;
import org.gms.server.quest.Quest;

/**
 * 处理打开物品培养界面（OPEN_ITEMUI）。
 * <p>对应操作码：{@link org.gms.net.opcodes.RecvOpcode#OPEN_ITEMUI}</p>
 */
public class RaiseUIStateHandler extends AbstractPacketHandler {

    /** 处理 打开物品界面 封包的业务逻辑。 */
    @Override
    public final void handlePacket(InPacket p, Client c) {
        int infoNumber = p.readShort();

        if (c.tryacquireClient()) {
            try {
                Character chr = c.getPlayer();
                Quest quest = Quest.getInstanceFromInfoNumber(infoNumber);
                QuestStatus mqs = chr.getQuest(quest);

                QuestScriptManager.getInstance().raiseOpen(c, (short) infoNumber, mqs.getNpc());

                if (mqs.getStatus() == QuestStatus.Status.NOT_STARTED) {
                    quest.forceStart(chr, 22000);
                    c.getAbstractPlayerInteraction().setQuestProgress(quest.getId(), infoNumber, 0);
                } else if (mqs.getStatus() == QuestStatus.Status.STARTED) {
                    chr.announceUpdateQuest(DelayedQuestUpdate.UPDATE, mqs, mqs.getInfoNumber() > 0);
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}