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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 地图小游戏对象（如 Omni 剪刀石头布）。
 */
public class MiniGame extends AbstractMapObject {
    private Character owner;
    private Character visitor;
    private final String password;
    private MiniGameType GameType = MiniGameType.UNDEFINED;
    private int piecetype;
    private int inprogress = 0;
    private final int[] piece = new int[250];
    private final List<Integer> list4x3 = new ArrayList<>();
    private final List<Integer> list5x4 = new ArrayList<>();
    private final List<Integer> list6x5 = new ArrayList<>();
    private final String description;
    private int loser = 1;
    private int firstslot = 0;
    private int visitorpoints = 0, visitorscore = 0, visitorforfeits = 0, lastvisitor = -1;
    private int ownerpoints = 0, ownerscore = 0, ownerforfeits = 0;
    private boolean visitorquit, ownerquit;
    private long nextavailabletie = 0;
    private int matchestowin = 0;

    public enum MiniGameType {
        UNDEFINED(0), OMOK(1), MATCH_CARD(2);
        private int value = 0;

        MiniGameType(int value) {
            this.value = value;
        }

        /**
         * 获取Value。
         * @return int 类型结果
         */
        public int getValue() {
            return value;
        }
    }

    public enum MiniGameResult {
        WIN, LOSS, TIE
    }

    /**
     * 构造 MiniGame 实例。
     * @param owner 归属角色
     * @param description description
     * @param password password
     */
    public MiniGame(Character owner, String description, String password) {
        this.owner = owner;
        this.description = description;
        this.password = password;
    }

    /**
     * 获取Password。
     * @return String 类型结果
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 检查Password。
     * @param sentPw sentPw
     * @return boolean 类型结果
     */
    public boolean checkPassword(String sentPw) {
        return this.password.length() == 0 || sentPw.toLowerCase().contentEquals(this.password.toLowerCase());
    }

    /**
     * 判断是否拥有Free、Slot。
     * @return boolean 类型结果
     */
    public boolean hasFreeSlot() {
        return visitor == null;
    }

    /**
     * 判断是否为归属者。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isOwner(Character chr) {
        return owner.equals(chr);
    }

    /**
     * 添加Visitor。
     * @param challenger challenger
     */
    public void addVisitor(Character challenger) {
        visitor = challenger;
        if (lastvisitor != challenger.getId()) {
            ownerscore = 0;
            ownerforfeits = 0;

            visitorscore = 0;
            visitorforfeits = 0;
            lastvisitor = challenger.getId();
        }

        Character owner = this.getOwner();
        if (GameType == MiniGameType.OMOK) {
            owner.sendPacket(PacketCreator.getMiniGameNewVisitor(this, challenger, 1));
            owner.getMap().broadcastMessage(PacketCreator.addOmokBox(owner, 2, 0));
        } else if (GameType == MiniGameType.MATCH_CARD) {
            owner.sendPacket(PacketCreator.getMatchCardNewVisitor(this, challenger, 1));
            owner.getMap().broadcastMessage(PacketCreator.addMatchCardBox(owner, 2, 0));
        }
    }

    /**
     * 执行 close、Room 操作。
     * @param forceClose forceClose
     */
    public void closeRoom(boolean forceClose) {
        owner.getMap().broadcastMessage(PacketCreator.removeMinigameBox(owner));

        if (forceClose) {
            this.broadcastToOwner(PacketCreator.getMiniGameClose(false, 4));
        }
        this.broadcastToVisitor(PacketCreator.getMiniGameClose(true, 3));

        if (visitor != null) {
            visitor.setMiniGame(null);
            visitor = null;
        }

        owner.setMiniGame(null);
        owner = null;
    }

    /**
     * 移除Visitor。
     * @param forceClose forceClose
     * @param challenger challenger
     */
    public void removeVisitor(boolean forceClose, Character challenger) {
        if (visitor == challenger) {
            if (isMatchInProgress()) { // owner is winner if visitor leave in progress
                minigameMatchOwnerWins(true);
            }
            if (forceClose) {
                visitor.sendPacket(PacketCreator.getMiniGameClose(true, 4));
            }

            challenger.setMiniGame(null);
            visitor = null;

            this.getOwner().sendPacket(PacketCreator.getMiniGameRemoveVisitor());
            if (GameType == MiniGameType.OMOK) {
                this.getOwner().getMap().broadcastMessage(PacketCreator.addOmokBox(owner, 1, 0));
            } else if (GameType == MiniGameType.MATCH_CARD) {
                this.getOwner().getMap().broadcastMessage(PacketCreator.addMatchCardBox(owner, 1, 0));
            }
        }
    }

    /**
     * 判断是否为Visitor。
     * @param challenger challenger
     * @return boolean 类型结果
     */
    public boolean isVisitor(Character challenger) {
        return visitor == challenger;
    }

    /**
     * 向地图广播到归属者。
     * @param packet 网络数据包
     */
    public void broadcastToOwner(Packet packet) {
        Client c = owner.getClient();
        if (c != null) {
            c.sendPacket(packet);
        }
    }

    /**
     * 向地图广播到、Visitor。
     * @param packet 网络数据包
     */
    public void broadcastToVisitor(Packet packet) {
        if (visitor != null) {
            visitor.sendPacket(packet);
        }
    }

    /**
     * 设置First、Slot。
     * @param type 类型
     */
    public void setFirstSlot(int type) {
        firstslot = type;
    }

    /**
     * 获取First、Slot。
     * @return int 类型结果
     */
    public int getFirstSlot() {
        return firstslot;
    }

    private void updateMiniGameBox() {
        this.getOwner().getMap().broadcastMessage(PacketCreator.addOmokBox(owner, visitor != null ? 2 : 1, inprogress));
    }

    private synchronized boolean minigameMatchFinish() {
        if (isMatchInProgress()) {
            inprogress = 0;
            return true;
        } else {
            return false;
        }
    }

    private void minigameMatchFinished() {
        updateMiniGameBox();

        if (ownerquit) {
            owner.closeMiniGame(true);
        } else if (visitorquit) {
            visitor.closeMiniGame(true);
        }
    }

    /**
     * 执行 minigame、Match、Started 操作。
     */
    public void minigameMatchStarted() {
        inprogress = 1;
        ownerquit = false;
        visitorquit = false;
    }

    /**
     * 设置Quit、After、游戏。
     * @param player 玩家
     * @param quit quit
     */
    public void setQuitAfterGame(Character player, boolean quit) {
        if (isOwner(player)) {
            ownerquit = quit;
        } else {
            visitorquit = quit;
        }
    }

    /**
     * 判断是否为Match、在、Progress。
     * @return boolean 类型结果
     */
    public boolean isMatchInProgress() {
        return inprogress != 0;
    }

    /**
     * 执行 deny、Tie 操作。
     * @param chr 角色
     */
    public void denyTie(Character chr) {
        if (this.isOwner(chr)) {
            inprogress |= (1 << 1);
        } else {
            inprogress |= (1 << 2);
        }
    }

    /**
     * 判断是否为Tie、Denied。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isTieDenied(Character chr) {
        if (this.isOwner(chr)) {
            return ((inprogress >> 2) % 2) == 1;
        } else {
            return ((inprogress >> 1) % 2) == 1;
        }
    }

    /**
     * 执行 minigame、Match、归属者、Wins 操作。
     * @param forfeit forfeit
     */
    public void minigameMatchOwnerWins(boolean forfeit) {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 1, this.isOmok());

        if (visitorforfeits < 4 || !forfeit) {
            ownerscore += 50;
        }
        visitorscore += (15 * (forfeit ? -1 : 1));
        if (forfeit) {
            visitorforfeits++;
        }

        this.broadcast(PacketCreator.getMiniGameOwnerWin(this, forfeit));

        minigameMatchFinished();
    }

    /**
     * 执行 minigame、Match、Visitor、Wins 操作。
     * @param forfeit forfeit
     */
    public void minigameMatchVisitorWins(boolean forfeit) {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 2, this.isOmok());

        if (ownerforfeits < 4 || !forfeit) {
            visitorscore += 50;
        }
        ownerscore += (15 * (forfeit ? -1 : 1));
        if (forfeit) {
            ownerforfeits++;
        }

        this.broadcast(PacketCreator.getMiniGameVisitorWin(this, forfeit));

        minigameMatchFinished();
    }

    /**
     * 执行 minigame、Match、Draw 操作。
     */
    public void minigameMatchDraw() {
        if (!minigameMatchFinish()) {
            return;
        }

        owner.setMiniGamePoints(visitor, 3, this.isOmok());

        long timeNow = Server.getInstance().getCurrentTime();
        if (nextavailabletie <= timeNow) {
            visitorscore += 10;
            ownerscore += 10;

            nextavailabletie = timeNow + MINUTES.toMillis(5);
        }

        this.broadcast(PacketCreator.getMiniGameTie(this));

        minigameMatchFinished();
    }

    /**
     * 设置归属者、Points。
     */
    public void setOwnerPoints() {
        ownerpoints++;
        if (ownerpoints + visitorpoints == matchestowin) {
            if (ownerpoints == visitorpoints) {
                minigameMatchDraw();
            } else if (ownerpoints > visitorpoints) {
                minigameMatchOwnerWins(false);
            } else {
                minigameMatchVisitorWins(false);
            }
            ownerpoints = 0;
            visitorpoints = 0;
        }
    }

    /**
     * 设置Visitor、Points。
     */
    public void setVisitorPoints() {
        visitorpoints++;
        if (ownerpoints + visitorpoints == matchestowin) {
            if (ownerpoints > visitorpoints) {
                minigameMatchOwnerWins(false);
            } else if (visitorpoints > ownerpoints) {
                minigameMatchVisitorWins(false);
            } else {
                minigameMatchDraw();
            }
            ownerpoints = 0;
            visitorpoints = 0;
        }
    }

    /**
     * 设置Matches、到、Win。
     * @param type 类型
     */
    public void setMatchesToWin(int type) {
        matchestowin = type;
    }

    /**
     * 设置Piece、类型。
     * @param type 类型
     */
    public void setPieceType(int type) {
        piecetype = type;
    }

    /**
     * 获取Piece、类型。
     * @return int 类型结果
     */
    public int getPieceType() {
        return piecetype;
    }

    /**
     * 设置游戏类型。
     * @param game game
     */
    public void setGameType(MiniGameType game) {
        GameType = game;
        if (GameType == MiniGameType.MATCH_CARD) {
            if (matchestowin == 6) {
                for (int i = 0; i < 6; i++) {
                    list4x3.add(i);
                    list4x3.add(i);
                }
            } else if (matchestowin == 10) {
                for (int i = 0; i < 10; i++) {
                    list5x4.add(i);
                    list5x4.add(i);
                }
            } else {
                for (int i = 0; i < 15; i++) {
                    list6x5.add(i);
                    list6x5.add(i);
                }
            }
        }
    }

    /**
     * 获取游戏类型。
     * @return MiniGameType 类型结果
     */
    public MiniGameType getGameType() {
        return GameType;
    }

    /**
     * 判断是否为Omok。
     * @return boolean 类型结果
     */
    public boolean isOmok() {
        return GameType.equals(MiniGameType.OMOK);
    }

    /**
     * 执行 shuffle、List 操作。
     */
    public void shuffleList() {
        if (matchestowin == 6) {
            Collections.shuffle(list4x3);
        } else if (matchestowin == 10) {
            Collections.shuffle(list5x4);
        } else {
            Collections.shuffle(list6x5);
        }
    }

    /**
     * 获取Card、ID。
     * @param slot slot
     * @return int 类型结果
     */
    public int getCardId(int slot) {
        int cardid;
        if (matchestowin == 6) {
            cardid = list4x3.get(slot);
        } else if (matchestowin == 10) {
            cardid = list5x4.get(slot);
        } else {
            cardid = list6x5.get(slot);
        }
        return cardid;
    }

    /**
     * 获取Matches、到、Win。
     * @return int 类型结果
     */
    public int getMatchesToWin() {
        return matchestowin;
    }

    /**
     * 设置Loser。
     * @param type 类型
     */
    public void setLoser(int type) {
        loser = type;
    }

    /**
     * 获取Loser。
     * @return int 类型结果
     */
    public int getLoser() {
        return loser;
    }

    /**
     * 执行 broadcast 操作。
     * @param packet 网络数据包
     */
    public void broadcast(Packet packet) {
        broadcastToOwner(packet);
        broadcastToVisitor(packet);
    }

    /**
     * 执行 chat 操作。
     * @param c c
     * @param chat chat
     */
    public void chat(Client c, String chat) {
        broadcast(PacketCreator.getPlayerShopChat(c.getPlayer(), chat, isOwner(c.getPlayer())));
    }

    /**
     * 执行 send、Omok 操作。
     * @param c c
     * @param type 类型
     */
    public void sendOmok(Client c, int type) {
        c.sendPacket(PacketCreator.getMiniGame(c, this, isOwner(c.getPlayer()), type));
    }

    /**
     * 执行 send、Match、Card 操作。
     * @param c c
     * @param type 类型
     */
    public void sendMatchCard(Client c, int type) {
        c.sendPacket(PacketCreator.getMatchCard(c, this, isOwner(c.getPlayer()), type));
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取Visitor。
     * @return Character 类型结果
     */
    public Character getVisitor() {
        return visitor;
    }

    /**
     * 设置Piece。
     * @param move1 move1
     * @param move2 move2
     * @param type 类型
     * @param chr 角色
     */
    public void setPiece(int move1, int move2, int type, Character chr) {
        int slot = move2 * 15 + move1 + 1;
        if (piece[slot] == 0) {
            piece[slot] = type;
            this.broadcast(PacketCreator.getMiniGameMoveOmok(this, move1, move2, type));
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 11; x++) {
                    if (searchCombo(x, y, type)) {
                        if (this.isOwner(chr)) {
                            this.minigameMatchOwnerWins(false);
                            this.setLoser(0);
                        } else {
                            this.minigameMatchVisitorWins(false);
                            this.setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = (y2 * 15 + x2 + 1);
                                piece[slot2] = 0;
                            }
                        }
                    }
                }
            }
            for (int y = 0; y < 15; y++) {
                for (int x = 4; x < 15; x++) {
                    if (searchCombo2(x, y, type)) {
                        if (this.isOwner(chr)) {
                            this.minigameMatchOwnerWins(false);
                            this.setLoser(0);
                        } else {
                            this.minigameMatchVisitorWins(false);
                            this.setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = (y2 * 15 + x2 + 1);
                                piece[slot2] = 0;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean searchCombo(int x, int y, int type) {
        int slot = y * 15 + x + 1;
        for (int i = 0; i < 5; i++) {
            if (piece[slot + i] == type) {
                if (i == 4) {
                    return true;
                }
            } else {
                break;
            }
        }
        for (int j = 15; j < 17; j++) {
            for (int i = 0; i < 5; i++) {
                if (piece[slot + i * j] == type) {
                    if (i == 4) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    private boolean searchCombo2(int x, int y, int type) {
        int slot = y * 15 + x + 1;
        for (int j = 14; j < 15; j++) {
            for (int i = 0; i < 5; i++) {
                if (piece[slot + i * j] == type) {
                    if (i == 4) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * 获取Description。
     * @return String 类型结果
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取归属者、Score。
     * @return int 类型结果
     */
    public int getOwnerScore() {
        return ownerscore;
    }

    /**
     * 获取Visitor、Score。
     * @return int 类型结果
     */
    public int getVisitorScore() {
        return visitorscore;
    }

    /**
     * 执行 send、Destroy、数据 操作。
     */
    @Override
    public void sendDestroyData(Client client) {}

    @Override
    public void sendSpawnData(Client client) {}

    @Override
    public MapObjectType getType() {
        return MapObjectType.MINI_GAME;
    }
}
