/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.dao.entity.NewyearDO;
import org.gms.manager.ServerManager;
import org.gms.net.server.Server;
import org.gms.server.TimerManager;
import org.gms.service.NewYearCardService;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.HOURS;

/**
 * 新年贺卡记录实体类
 * 用于管理游戏中的新年贺卡系统，包括贺卡的发送、接收、保存、定时提醒和删除功能
 *
 * @author Ronan - credits to Eric for showing the New Year opcodes and handler layout
 */
@Slf4j
@Data
public class NewYearCardRecord {
    /**
     * 贺卡记录ID
     */
    private int id;

    /**
     * 发送者角色ID
     */
    private int senderId;

    /**
     * 发送者角色名称
     */
    private String senderName;

    /**
     * 发送者是否已丢弃贺卡
     */
    private boolean senderDiscardCard;

    /**
     * 接收者角色ID
     */
    private int receiverId;

    /**
     * 接收者角色名称
     */
    private String receiverName;

    /**
     * 接收者是否已丢弃贺卡
     */
    private boolean receiverDiscardCard;

    /**
     * 接收者是否已收到贺卡
     */
    private boolean receiverReceivedCard;

    /**
     * 贺卡消息内容
     */
    private String message;

    /**
     * 发送时间戳（毫秒）
     */
    private long dateSent;

    /**
     * 接收时间戳（毫秒）
     */
    private long dateReceived;

    /**
     * 定时发送任务，用于定时提醒接收者
     */
    private ScheduledFuture<?> sendTask;

    /**
     * 新年贺卡服务Bean，通过Spring上下文获取
     */
    private static final NewYearCardService newYearCardService = ServerManager.getApplicationContext().getBean(NewYearCardService.class);

    /**
     * 构造函数，创建新的新年贺卡
     * @param senderId 发送者角色ID
     * @param senderName 发送者角色名称
     * @param receiverId 接收者角色ID
     * @param receiverName 接收者角色名称
     * @param message 贺卡消息内容
     */
    public NewYearCardRecord(int senderId, String senderName, int receiverId, String receiverName, String message) {
        this.id = -1;

        this.senderId = senderId;
        this.senderName = senderName;
        this.senderDiscardCard = false;

        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverDiscardCard = false;
        this.receiverReceivedCard = false;

        this.message = message;

        this.dateSent = System.currentTimeMillis();
        this.dateReceived = 0;
    }

    /**
     * 设置额外的贺卡记录信息（从数据库加载时使用）
     * @param id 贺卡记录ID
     * @param senderDiscardCard 发送者是否已丢弃
     * @param receiverDiscardCard 接收者是否已丢弃
     * @param receiverReceivedCard 接收者是否已收到
     * @param dateSent 发送时间戳
     * @param dateReceived 接收时间戳
     */
    public void setExtraNewYearCardRecord(int id, boolean senderDiscardCard, boolean receiverDiscardCard, boolean receiverReceivedCard, long dateSent, long dateReceived) {
        this.id = id;
        this.senderDiscardCard = senderDiscardCard;
        this.receiverDiscardCard = receiverDiscardCard;
        this.receiverReceivedCard = receiverReceivedCard;

        this.dateSent = dateSent;
        this.dateReceived = dateReceived;
    }

    /**
     * 保存新的新年贺卡到数据库
     * @param newyear 要保存的新年贺卡对象
     */
    public static void saveNewYearCard(NewYearCardRecord newyear) {
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO newyear VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, newyear.senderId);
                ps.setString(2, newyear.senderName);
                ps.setInt(3, newyear.receiverId);
                ps.setString(4, newyear.receiverName);

                ps.setString(5, newyear.message);

                ps.setBoolean(6, newyear.senderDiscardCard);
                ps.setBoolean(7, newyear.receiverDiscardCard);
                ps.setBoolean(8, newyear.receiverReceivedCard);

                ps.setLong(9, newyear.dateSent);
                ps.setLong(10, newyear.dateReceived);

                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    newyear.id = rs.getInt(1);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 更新新年贺卡状态为已接收
     * @param newyear 要更新的新年贺卡对象
     */
    public static void updateNewYearCard(NewYearCardRecord newyear) {
        newyear.receiverReceivedCard = true;
        newyear.dateReceived = System.currentTimeMillis();

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE newyear SET received=1, timereceived=? WHERE id=?")) {
                ps.setLong(1, newyear.dateReceived);
                ps.setInt(2, newyear.id);

                ps.executeUpdate();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 根据贺卡ID加载新年贺卡记录
     * 首先从服务器内存缓存中查找，找不到则从数据库加载并缓存
     * @param cardid 贺卡记录ID
     * @return 加载的新年贺卡对象，如果不存在则返回null
     */
    public static NewYearCardRecord loadNewYearCard(int cardid) {
        NewYearCardRecord nyc = Server.getInstance().getNewYearCard(cardid);
        if (nyc != null) {
            return nyc;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM newyear WHERE id = ?")) {
                ps.setInt(1, cardid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        NewYearCardRecord newyear = new NewYearCardRecord(rs.getInt("senderid"), rs.getString("sendername"), rs.getInt("receiverid"), rs.getString("receivername"), rs.getString("message"));
                        newyear.setExtraNewYearCardRecord(rs.getInt("id"), rs.getBoolean("senderdiscard"), rs.getBoolean("receiverdiscard"), rs.getBoolean("received"), rs.getLong("timesent"), rs.getLong("timereceived"));

                        Server.getInstance().setNewYearCard(newyear);
                        return newyear;
                    }
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return null;
    }

    /**
     * 加载指定角色的所有新年贺卡记录
     * @param chr 角色对象
     */
    public static void loadPlayerNewYearCards(Character chr) {
        try {
            List<NewyearDO> newyearDOList = newYearCardService.loadPlayerNewYearCards(chr);
            newyearDOList.forEach(newyearDO -> {
                NewYearCardRecord newYearCardRecord = new NewYearCardRecord(newyearDO.getSenderid(), newyearDO.getSendername(),
                        newyearDO.getReceiverid(), newyearDO.getReceivername(), newyearDO.getMessage());
                newYearCardRecord.setExtraNewYearCardRecord(newyearDO.getId().intValue(), newyearDO.getSenderdiscard(),
                        newyearDO.getReceiverdiscard(), newyearDO.getReceived(), newyearDO.getTimesent(), newyearDO.getTimereceived());
                chr.addNewYearRecord(newYearCardRecord);
            });
        } catch (Exception e) {
            log.error("loadPlayerNewYearCards error", e);
        }
    }

    /**
     * 在游戏中打印角色的所有新年贺卡记录（用于调试或GM命令）
     * @param chr 角色对象
     */
    public static void printNewYearRecords(Character chr) {
        chr.dropMessage(5, "New Years: " + chr.getNewYearRecords().size());

        for (NewYearCardRecord nyc : chr.getNewYearRecords()) {
            chr.dropMessage(5, "-------------------------------");

            chr.dropMessage(5, "Id: " + nyc.id);

            chr.dropMessage(5, "Sender id: " + nyc.senderId);
            chr.dropMessage(5, "Sender name: " + nyc.senderName);
            chr.dropMessage(5, "Sender discard: " + nyc.senderDiscardCard);

            chr.dropMessage(5, "Receiver id: " + nyc.receiverId);
            chr.dropMessage(5, "Receiver name: " + nyc.receiverName);
            chr.dropMessage(5, "Receiver discard: " + nyc.receiverDiscardCard);
            chr.dropMessage(5, "Received: " + nyc.receiverReceivedCard);

            chr.dropMessage(5, "Message: " + nyc.message);
            chr.dropMessage(5, "Date sent: " + nyc.dateSent);
            chr.dropMessage(5, "Date recv: " + nyc.dateReceived);
        }
    }

    /**
     * 启动新年贺卡定时提醒任务
     * 每小时检查一次接收者是否在线，如果在线则发送提醒数据包
     */
    public void startNewYearCardTask() {
        if (sendTask != null) {
            return;
        }

        sendTask = TimerManager.getInstance().register(() -> {
            Server server = Server.getInstance();

            int world = server.getCharacterWorld(receiverId);
            if (world == -1) {
                sendTask.cancel(false);
                sendTask = null;

                return;
            }

            Character target = server.getWorld(world).getPlayerStorage().getCharacterById(receiverId);
            if (target != null && target.isLoggedInWorld()) {
                target.sendPacket(PacketCreator.onNewYearCardRes(target, NewYearCardRecord.this, 0xC, 0));
            }
        }, HOURS.toMillis(1));
    }

    /**
     * 停止新年贺卡定时提醒任务
     */
    public void stopNewYearCardTask() {
        if (sendTask != null) {
            sendTask.cancel(false);
            sendTask = null;
        }
    }

    /**
     * 从服务器缓存和数据库中删除新年贺卡
     * @param id 要删除的贺卡ID
     */
    private static void deleteNewYearCard(int id) {
        Server.getInstance().removeNewYearCard(id);

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM newyear WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 删除角色的所有新年贺卡（作为发送者或接收者）
     * @param send true表示作为发送者删除，false表示作为接收者删除
     * @param chr 角色对象
     */
    public static void removeAllNewYearCard(boolean send, Character chr) {
        int cid = chr.getId();
        
        /* not truly needed since it's going to be hard removed from the DB
        String actor = (send ? "sender" : "receiver");
        
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE newyear SET " + actor + "id = 1, received = 0 WHERE " + actor + "id = ?")) {
                ps.setInt(1, cid);
                ps.executeUpdate();
            }
        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
        */

        Set<NewYearCardRecord> set = new HashSet<>(chr.getNewYearRecords());
        for (NewYearCardRecord nyc : set) {
            if (send) {
                if (nyc.senderId == cid) {
                    nyc.senderDiscardCard = true;
                    nyc.receiverReceivedCard = false;

                    chr.removeNewYearRecord(nyc);
                    deleteNewYearCard(nyc.id);

                    chr.getMap().broadcastMessage(PacketCreator.onNewYearCardRes(chr, nyc, 0xE, 0));

                    Character other = chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.getReceiverId());
                    if (other != null && other.isLoggedInWorld()) {
                        other.removeNewYearRecord(nyc);
                        other.getMap().broadcastMessage(PacketCreator.onNewYearCardRes(other, nyc, 0xE, 0));

                        other.dropMessage(6, "[New Year] " + chr.getName() + " threw away the New Year card.");
                    }
                }
            } else {
                if (nyc.receiverId == cid) {
                    nyc.receiverDiscardCard = true;
                    nyc.receiverReceivedCard = false;

                    chr.removeNewYearRecord(nyc);
                    deleteNewYearCard(nyc.id);

                    chr.getMap().broadcastMessage(PacketCreator.onNewYearCardRes(chr, nyc, 0xE, 0));

                    Character other = chr.getClient().getWorldServer().getPlayerStorage().getCharacterById(nyc.getSenderId());
                    if (other != null && other.isLoggedInWorld()) {
                        other.removeNewYearRecord(nyc);
                        other.getMap().broadcastMessage(PacketCreator.onNewYearCardRes(other, nyc, 0xE, 0));

                        other.dropMessage(6, "[New Year] " + chr.getName() + " threw away the New Year card.");
                    }
                }
            }
        }
    }
}
