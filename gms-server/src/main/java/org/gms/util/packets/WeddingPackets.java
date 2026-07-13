/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.util.packets;

import org.gms.client.Character;
import org.gms.client.inventory.Item;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.net.opcodes.SendOpcode;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;
import org.gms.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 婚礼系统数据包构建类
 * <p>
 * 包含婚礼场地、婚礼拍照、婚礼管理等相关的数据结构和数据包构建方法，
 * 以及婚姻状态、婚礼请求、婚礼类型、婚礼地图、婚礼物品等枚举定义。
 * </p>
 *
 * @author Eric
 * @author Drago (Dragohe4rt) - Wishlists edited
 */
public class WeddingPackets extends PacketCreator {
    private static final Logger log = LoggerFactory.getLogger(WeddingPackets.class);

    /**
     * 婚礼场地状态数据结构
     */
    public class Field_Wedding {
        /** 公告次数 */
        public int m_nNoticeCount;
        /** 当前阶段 */
        public int m_nCurrentStep;
        /** 祝福开始时间 */
        public int m_nBlessStartTime;
    }

    /**
     * 婚礼拍照场地状态数据结构
     */
    public class Field_WeddingPhoto {
        /** 是否已拍照 */
        public boolean m_bPictureTook;
    }

    /**
     * 婚礼预约数据结构
     */
    public class GW_WeddingReservation {
        /** 预约编号 */
        public int dwReservationNo;
        /** 新郎ID、新娘ID */
        public int dwGroom, dwBride;
        /** 新郎姓名、新娘姓名 */
        public String sGroomName, sBrideName;
        /** 婚礼类型 */
        public int usWeddingType;
    }

    /**
     * 婚礼心愿单数据结构
     */
    public class WeddingWishList {
        /** 关联的玩家角色 */
        public Character pUser;
        /** 婚姻编号 */
        public int dwMarriageNo;
        /** 性别 */
        public int nGender;
        /** 心愿单类型 */
        public int nWLType;
        /** 槽位数量 */
        public int nSlotCount;
        /** 心愿物品列表 */
        public List<String> asWishList = new ArrayList<>();
        /** 修改标志 */
        public int usModifiedFlag;
        /** 是否已加载 */
        public boolean bLoaded;
    }

    /**
     * 婚礼心愿单数据（网络传输用）
     */
    public class GW_WeddingWishList {
        /** 心愿单最大物品数 */
        public final int WEDDINGWL_MAX = 0xA;
        /** 预约编号 */
        public int dwReservationNo;
        /** 性别 */
        public byte nGender;
        /** 物品名称 */
        public String sItemName;
    }

    /**
     * 婚姻状态枚举
     */
    public enum MarriageStatus {
        /** 单身 */
        SINGLE(0x0),
        /** 已订婚 */
        ENGAGED(0x1),
        /** 已预约 */
        RESERVED(0x2),
        /** 已婚 */
        MARRIED(0x3);

        /** 状态值 */
        private final int ms;

        MarriageStatus(int ms) {
            this.ms = ms;
        }

        public int getMarriageStatus() {
            return ms;
        }
    }

    /**
     * 婚姻请求类型枚举
     */
    public enum MarriageRequest {
        /** 添加婚姻记录 */
        AddMarriageRecord(0x0),
        /** 设置婚姻记录 */
        SetMarriageRecord(0x1),
        /** 删除婚姻记录 */
        DeleteMarriageRecord(0x2),
        /** 加载预约 */
        LoadReservation(0x3),
        /** 添加预约 */
        AddReservation(0x4),
        /** 删除预约 */
        DeleteReservation(0x5),
        /** 获取预约 */
        GetReservation(0x6);

        /** 请求值 */
        private final int req;

        MarriageRequest(int req) {
            this.req = req;
        }

        public int getMarriageRequest() {
            return req;
        }
    }

    /**
     * 婚礼类型枚举
     */
    public enum WeddingType {
        /** 大教堂 */
        CATHEDRAL(0x1),
        /** 拉斯维加斯教堂 */
        VEGAS(0x2),
        /** 大教堂高级版 */
        CATHEDRAL_PREMIUM(0xA),
        /** 大教堂普通版 */
        CATHEDRAL_NORMAL(0xB),
        /** 拉斯维加斯高级版 */
        VEGAS_PREMIUM(0x14),
        /** 拉斯维加斯普通版 */
        VEGAS_NORMAL(0x15);

        /** 类型值 */
        private final int wt;

        WeddingType(int wt) {
            this.wt = wt;
        }

        public int getType() {
            return wt;
        }
    }

    /**
     * 婚礼相关地图枚举
     */
    public enum WeddingMap {
        /** 婚礼城镇（阿莫利亚） */
        WEDDINGTOWN(MapId.AMORIA),
        /** 教堂婚礼祭坛起点 */
        CHAPEL_STARTMAP(MapId.CHAPEL_WEDDING_ALTAR),
        /** 大教堂婚礼祭坛起点 */
        CATHEDRAL_STARTMAP(MapId.CATHEDRAL_WEDDING_ALTAR),
        /** 拍照地图 */
        PHOTOMAP(MapId.WEDDING_PHOTO),
        /** 退出地图 */
        EXITMAP(MapId.WEDDING_EXIT);

        /** 地图ID */
        private final int wm;

        WeddingMap(int wm) {
            this.wm = wm;
        }

        public int getMap() {
            return wm;
        }
    }

    /**
     * 婚礼相关物品枚举
     */
    public enum WeddingItem {
        /** 月光石结婚戒指 */
        WR_MOONSTONE(ItemId.WEDDING_RING_MOONSTONE),
        /** 星宝石结婚戒指 */
        WR_STARGEM(ItemId.WEDDING_RING_STAR),
        /** 金心结婚戒指 */
        WR_GOLDENHEART(ItemId.WEDDING_RING_GOLDEN),
        /** 银天鹅结婚戒指 */
        WR_SILVERSWAN(ItemId.WEDDING_RING_SILVER),
        /** 月光石订婚戒指盒 */
        ERB_MOONSTONE(ItemId.ENGAGEMENT_BOX_MOONSTONE),
        /** 星宝石订婚戒指盒 */
        ERB_STARGEM(ItemId.ENGAGEMENT_BOX_STAR),
        /** 金心订婚戒指盒 */
        ERB_GOLDENHEART(ItemId.ENGAGEMENT_BOX_GOLDEN),
        /** 银天鹅订婚戒指盒 */
        ERB_SILVERSWAN(ItemId.ENGAGEMENT_BOX_SILVER),
        /** 空的月光石订婚戒指盒 */
        ERBE_MOONSTONE(ItemId.EMPTY_ENGAGEMENT_BOX_MOONSTONE),
        /** 月光石订婚戒指 */
        ER_MOONSTONE(ItemId.ENGAGEMENT_RING_MOONSTONE),
        /** 空的星宝石订婚戒指盒 */
        ERBE_STARGEM(ItemId.EMPTY_ENGAGEMENT_BOX_STAR),
        /** 星宝石订婚戒指 */
        ER_STARGEM(ItemId.ENGAGEMENT_RING_STAR),
        /** 空的金心订婚戒指盒 */
        ERBE_GOLDENHEART(ItemId.EMPTY_ENGAGEMENT_BOX_GOLDEN),
        /** 金心订婚戒指 */
        ER_GOLDENHEART(ItemId.ENGAGEMENT_RING_GOLDEN),
        /** 空的银天鹅订婚戒指盒 */
        ERBE_SILVERSWAN(ItemId.EMPTY_ENGAGEMENT_BOX_SILVER),
        /** 银天鹅订婚戒指 */
        ER_SILVERSWAN(ItemId.ENGAGEMENT_RING_SILVER),
        /** 父母的祝福 */
        PARENTS_BLESSING(ItemId.PARENTS_BLESSING),
        /** 主婚人许可 */
        OFFICIATORS_PERMISSION(ItemId.OFFICIATORS_PERMISSION),
        /** 大教堂高级预约收据 */
        WR_CATHEDRAL_PREMIUM(ItemId.PREMIUM_CATHEDRAL_RESERVATION_RECEIPT),
        /** 教堂高级预约收据 */
        WR_VEGAS_PREMIUM(ItemId.PREMIUM_CHAPEL_RESERVATION_RECEIPT),
        /** 教堂邀请函 */
        IB_VEGAS(ItemId.INVITATION_CHAPEL),
        /** 大教堂邀请函 */
        IB_CATHEDRAL(ItemId.INVITATION_CATHEDRAL),
        /** 收到的教堂邀请函 */
        IG_VEGAS(ItemId.RECEIVED_INVITATION_CHAPEL),
        /** 收到的大教堂邀请函 */
        IG_CATHEDRAL(ItemId.RECEIVED_INVITATION_CATHEDRAL),
        /** 情侣缟玛瑙宝箱 */
        OB_FORCOUPLE(ItemId.ONYX_CHEST_FOR_COUPLE),
        /** 大教堂普通预约收据 */
        WR_CATHEDRAL_NORMAL(ItemId.NORMAL_CATHEDRAL_RESERVATION_RECEIPT),
        /** 教堂普通预约收据 */
        WR_VEGAS_NORMAL(ItemId.NORMAL_CHAPEL_RESERVATION_RECEIPT),
        /** 大教堂普通婚礼票 */
        WT_CATHEDRAL_NORMAL(ItemId.NORMAL_WEDDING_TICKET_CATHEDRAL),
        /** 教堂普通婚礼票 */
        WT_VEGAS_NORMAL(ItemId.NORMAL_WEDDING_TICKET_CHAPEL),
        /** 教堂高级婚礼票 */
        WT_VEGAS_PREMIUM(ItemId.PREMIUM_WEDDING_TICKET_CHAPEL),
        /** 大教堂高级婚礼票 */
        WT_CATHEDRAL_PREMIUM(ItemId.PREMIUM_WEDDING_TICKET_CATHEDRAL);

        /** 物品ID */
        private final int wi;

        WeddingItem(int wi) {
            this.wi = wi;
        }

        public int getItem() {
            return wi;
        }
    }

    /**
     * <name> has requested engagement. Will you accept this proposal?
     *
     * @param name
     * @param playerid
     * @return mplew
     */
    public static Packet onMarriageRequest(String name, int playerid) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_REQUEST);
        p.writeByte(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        p.writeString(name); // name
        p.writeInt(playerid); // playerid
        return p;
    }

    /**
     * A quick rundown of how (I think based off of enough BMS searching) WeddingPhoto_OnTakePhoto works:
     * - We send this packet with (first) the Groom / Bride IGNs
     * - We then send a fieldId (unsure about this part at the moment, 90% sure it's the id of the map)
     * - After this, we write an integer of the amount of characters within the current map (which is the Cake Map -- exclude users within Exit Map)
     * - Once we've retrieved the size of the characters, we begin to write information about them (Encode their name, guild, etc info)
     * - Now that we've Encoded our character data, we begin to Encode the ScreenShotPacket which requires a TemplateID, IGN, and their positioning
     * - Finally, after encoding all of our data, we send this packet out to a MapGen application server
     * - The MapGen server will then retrieve the packet byte array and convert the bytes into a ImageIO 2D JPG output
     * - The result after converting into a JPG will then be remotely uploaded to /weddings/ with ReservedGroomName_ReservedBrideName to be displayed on the web server.
     * <p>
     * - Will no longer continue Wedding Photos, needs a WvsMapGen :(
     *
     * @param ReservedGroomName The groom IGN of the wedding
     * @param ReservedBrideName The bride IGN of the wedding
     * @param m_dwField         The current field id (the id of the cake map, ex. 680000300)
     * @param m_uCount          The current user count (equal to m_dwUsers.size)
     * @param m_dwUsers         The List of all Character guests within the current cake map to be encoded
     * @return mplew (MaplePacket) Byte array to be converted and read for byte[]->ImageIO
     */
    public static Packet onTakePhoto(String ReservedGroomName, String ReservedBrideName, int m_dwField, List<Character> m_dwUsers) { // OnIFailedAtWeddingPhotos
        OutPacket p = OutPacket.create(SendOpcode.WEDDING_PHOTO);// v53 header, convert -> v83
        p.writeString(ReservedGroomName);
        p.writeString(ReservedBrideName);
        p.writeInt(m_dwField); // field id?
        p.writeInt(m_dwUsers.size());

        for (Character guest : m_dwUsers) {
            // Begin Avatar Encoding
            addCharLook(p, guest, false); // CUser::EncodeAvatar
            p.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new groom marriage ID??
            p.writeInt(30000); // v20 = *(_DWORD *)(v13 + 2192) -- new bride marriage ID??
            p.writeString(guest.getName());
            p.writeString(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getName() : "");
            p.writeShort(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoBG() : 0);
            p.writeByte(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoBGColor() : 0);
            p.writeShort(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogo() : 0);
            p.writeByte(guest.getGuildId() > 0 && guest.getGuild() != null ? guest.getGuild().getLogoColor() : 0);
            p.writeShort(guest.getPosition().x); // v18 = *(_DWORD *)(v13 + 3204);
            p.writeShort(guest.getPosition().y); // v20 = *(_DWORD *)(v13 + 3208);
            // Begin Screenshot Encoding
            p.writeByte(1); // // if ( *(_DWORD *)(v13 + 288) ) { COutPacket::Encode1(&thisa, v20);
            // CPet::EncodeScreenShotPacket(*(CPet **)(v13 + 288), &thisa);
            p.writeInt(1); // dwTemplateID
            p.writeString(guest.getName()); // m_sName
            p.writeShort(guest.getPosition().x); // m_ptCurPos.x
            p.writeShort(guest.getPosition().y); // m_ptCurPos.y
            p.writeByte(guest.getStance()); // guest.m_bMoveAction
        }

        return p;
    }

    /**
     * Enable spouse chat and their engagement ring without @relog
     *
     * @param marriageId
     * @param chr
     * @param wedding
     * @return mplew
     */
    public static Packet OnMarriageResult(int marriageId, Character chr, boolean wedding) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(11);
        p.writeInt(marriageId);
        p.writeInt(chr.getGender() == 0 ? chr.getId() : chr.getPartnerId());
        p.writeInt(chr.getGender() == 0 ? chr.getPartnerId() : chr.getId());
        p.writeShort(wedding ? 3 : 1);
        if (wedding) {
            p.writeInt(chr.getMarriageItemId());
            p.writeInt(chr.getMarriageItemId());
        } else {
            p.writeInt(ItemId.WEDDING_RING_MOONSTONE); // Engagement Ring's Outcome (doesn't matter for engagement)
            p.writeInt(ItemId.WEDDING_RING_MOONSTONE); // Engagement Ring's Outcome (doesn't matter for engagement)
        }
        p.writeFixedString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? chr.getName() : Character.getNameById(chr.getPartnerId()), '\0', 13));
        p.writeFixedString(StringUtil.getRightPaddedStr(chr.getGender() == 0 ? Character.getNameById(chr.getPartnerId()) : chr.getName(), '\0', 13));

        return p;
    }

    /**
     * To exit the Engagement Window (Waiting for her response...), we send a GMS-like pop-up.
     *
     * @param msg
     * @return mplew
     */
    public static Packet OnMarriageResult(final byte msg) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(msg);
        if (msg == 36) {
            p.writeByte(1);
            p.writeString("You are now engaged.");
        }
        return p;
    }

    /**
     * The World Map includes 'loverPos' in which this packet controls
     *
     * @param partner
     * @param mapid
     * @return mplew
     */
    public static Packet OnNotifyWeddingPartnerTransfer(int partner, int mapid) {
        OutPacket p = OutPacket.create(SendOpcode.NOTIFY_MARRIED_PARTNER_MAP_TRANSFER);
        p.writeInt(mapid);
        p.writeInt(partner);
        return p;
    }

    /**
     * The wedding packet to display Pelvis Bebop and enable the Wedding Ceremony Effect between two characters
     * CField_Wedding::OnWeddingProgress - Stages
     * CField_Wedding::OnWeddingCeremonyEnd - Wedding Ceremony Effect
     *
     * @param setBlessEffect
     * @param groom
     * @param bride
     * @param step
     * @return mplew
     */
    public static Packet OnWeddingProgress(boolean setBlessEffect, int groom, int bride, byte step) {
        OutPacket p = OutPacket.create(setBlessEffect ? SendOpcode.WEDDING_CEREMONY_END : SendOpcode.WEDDING_PROGRESS);
        if (!setBlessEffect) { // in order for ceremony packet to send, byte step = 2 must be sent first
            p.writeByte(step);
        }
        p.writeInt(groom);
        p.writeInt(bride);
        return p;
    }

    /**
     * When we open a Wedding Invitation, we display the Bride & Groom
     *
     * @param groom
     * @param bride
     * @return mplew
     */
    public static Packet sendWeddingInvitation(String groom, String bride) {
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_RESULT);
        p.writeByte(15);
        p.writeString(groom);
        p.writeString(bride);
        p.writeShort(1); // 0 = Cathedral Normal?, 1 = Cathedral Premium?, 2 = Chapel Normal?
        return p;
    }

    public static Packet sendWishList() { // fuck my life
        OutPacket p = OutPacket.create(SendOpcode.MARRIAGE_REQUEST);
        p.writeByte(9);
        return p;
    }

    /**
     * Handles all of WeddingWishlist packets
     *
     * @param mode
     * @param itemnames
     * @param items
     * @return mplew
     */
    public static Packet onWeddingGiftResult(byte mode, List<String> itemnames, List<Item> items) {
        OutPacket p = OutPacket.create(SendOpcode.WEDDING_GIFT_RESULT);
        p.writeByte(mode);
        switch (mode) {
            case 0xC: // 12 : You cannot give more than one present for each wishlist 
            case 0xE: // 14 : Failed to send the gift.
                break;

            case 0x09: { // Load Wedding Registry
                p.writeByte(itemnames.size());
                for (String names : itemnames) {
                    p.writeString(names);
                }
                break;
            }
            case 0xA: // Load Bride's Wishlist 
            case 0xF: // 10, 15, 16 = CWishListRecvDlg::OnPacket
            case 0xB: { // Add Item to Wedding Registry 
                // 11 : You have sent a gift | | 13 : Failed to send the gift. | 
                if (mode == 0xB) {
                    p.writeByte(itemnames.size());
                    for (String names : itemnames) {
                        p.writeString(names);
                    }
                }
                p.writeLong(32);
                p.writeByte(items.size());
                for (Item item : items) {
                    addItemInfo(p, item, true);
                }
                break;
            }
            default: {
                log.warn("Unknown Wishlist Mode: {}", mode);
                break;
            }
        }
        return p;
    }
} 