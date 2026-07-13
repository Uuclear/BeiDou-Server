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
package org.gms.net;

import org.gms.constants.net.ServerConstants;
import org.gms.net.netty.LoginServer;
import org.gms.net.opcodes.Opcode;
import org.gms.net.opcodes.RecvOpcode;
import org.gms.net.server.channel.handlers.*;
import org.gms.net.server.handlers.CustomPacketHandler;
import org.gms.net.server.handlers.KeepAliveHandler;
import org.gms.net.server.handlers.LoginRequiringNoOpHandler;
import org.gms.net.server.handlers.login.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 数据包处理器注册表
 * 负责管理和分发所有接收到的客户端数据包到对应的处理器
 * 根据服务器类型（登录服务器/频道服务器）注册不同的处理器集合
 * 使用单例模式，每个世界/频道组合维护一个处理器实例
 *
 * @author OdinMS开发团队
 */
public final class PacketProcessor {
    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(PacketProcessor.class);

    /**
     * 处理器实例缓存，键为"世界ID 频道ID"格式的字符串
     */
    private static final Map<String, PacketProcessor> instances = new LinkedHashMap<>();

    /**
     * 频道服务器依赖注入
     */
    private static ChannelDependencies channelDeps;

    /**
     * 数据包处理器数组，索引为数据包操作码（Opcode）
     */
    private PacketHandler[] handlers;

    /**
     * 私有构造函数，初始化处理器数组
     * 计算最大的接收操作码值，以此确定数组大小
     */
    private PacketProcessor() {
        int maxRecvOp = 0;
        for (RecvOpcode op : RecvOpcode.values()) {
            if (op.getValue() > maxRecvOp) {
                maxRecvOp = op.getValue();
            }
        }
        handlers = new PacketHandler[maxRecvOp + 1];
    }

    /**
     * 注册游戏处理器所需的依赖
     *
     * @param channelDependencies 频道服务器依赖对象，包含各种服务实例
     */
    public static void registerGameHandlerDependencies(ChannelDependencies channelDependencies) {
        PacketProcessor.channelDeps = channelDependencies;
    }

    /**
     * 获取登录服务器的数据包处理器
     * 登录服务器使用固定的世界ID和频道ID
     *
     * @return 登录服务器的数据包处理器实例
     */
    public static PacketProcessor getLoginServerProcessor() {
        return getProcessor(LoginServer.WORLD_ID, LoginServer.CHANNEL_ID);
    }

    /**
     * 获取指定频道服务器的数据包处理器
     *
     * @param world 世界ID
     * @param channel 频道ID
     * @return 对应频道的数据包处理器实例
     * @throws IllegalStateException 如果依赖未注册则抛出异常
     */
    public static PacketProcessor getChannelServerProcessor(int world, int channel) {
        if (channelDeps == null) {
            throw new IllegalStateException("Unable to get channel server processor - dependencies are not registered");
        }

        return getProcessor(world, channel);
    }

    /**
     * 根据数据包ID获取对应的处理器
     *
     * @param packetId 数据包操作码ID
     * @return 对应的数据包处理器，如果ID无效则返回null
     */
    public PacketHandler getHandler(short packetId) {
        if (packetId < 0 || packetId >= handlers.length) {
            return null;
        }
        PacketHandler handler = handlers[packetId];
        return handler;
    }

    /**
     * 注册一个数据包处理器
     *
     * @param code 数据包操作码
     * @param handler 对应的处理器实例
     */
    public void registerHandler(Opcode code, PacketHandler handler) {
        try {
            handlers[code.getValue()] = handler;
        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("Error registering handler {}", code.getName(), e);
        }
    }

    /**
     * 获取或创建指定世界和频道的数据包处理器（同步方法）
     * 如果处理器不存在则创建新实例并初始化
     *
     * @param world 世界ID
     * @param channel 频道ID
     * @return 数据包处理器实例
     */
    public synchronized static PacketProcessor getProcessor(int world, int channel) {
        final String processorId = world + " " + channel;
        PacketProcessor processor = instances.get(processorId);
        if (processor == null) {
            processor = new PacketProcessor();
            processor.reset(channel);
            instances.put(processorId, processor);
        }
        return processor;
    }

    /**
     * 重置并重新注册处理器
     * 根据服务器版本和频道类型注册对应的处理器集合
     *
     * @param channel 频道ID，负数表示登录服务器
     */
    public void reset(int channel) {
        handlers = new PacketHandler[handlers.length];

        switch (ServerConstants.VERSION) {
            case 83:
                registerCommonHandlers();

                if (channel < 0) {
                    registerLoginHandlers();
                } else {
                    registerChannelHandlers();
                }
                break;
            default:
                log.warn("找不到指定的版本注册 Opcode Handler");
                break;
        }
    }

    /**
     * 注册通用处理器
     * 登录服务器和频道服务器都需要的处理器（如心跳包、自定义包等）
     */
    private void registerCommonHandlers() {
        registerHandler(RecvOpcode.PONG, new KeepAliveHandler());
        registerHandler(RecvOpcode.CUSTOM_PACKET, new CustomPacketHandler());
    }

    /**
     * 注册登录服务器专用处理器
     * 处理登录、角色选择、角色创建/删除等登录阶段的数据包
     */
    private void registerLoginHandlers() {
        registerHandler(RecvOpcode.ACCEPT_TOS, new AcceptToSHandler());
        registerHandler(RecvOpcode.AFTER_LOGIN, new AfterLoginHandler());
        registerHandler(RecvOpcode.SERVERLIST_REREQUEST, new ServerlistRequestHandler());
        registerHandler(RecvOpcode.CHARLIST_REQUEST, new CharlistRequestHandler());
        registerHandler(RecvOpcode.CHAR_SELECT, new CharSelectedHandler());
        registerHandler(RecvOpcode.LOGIN_PASSWORD, new LoginPasswordHandler());
        registerHandler(RecvOpcode.RELOG, new RelogRequestHandler());
        registerHandler(RecvOpcode.SERVERLIST_REQUEST, new ServerlistRequestHandler());
        registerHandler(RecvOpcode.SERVERSTATUS_REQUEST, new ServerStatusRequestHandler());
        registerHandler(RecvOpcode.CHECK_CHAR_NAME, new CheckCharNameHandler());
        registerHandler(RecvOpcode.CREATE_CHAR, new CreateCharHandler());
        registerHandler(RecvOpcode.DELETE_CHAR, new DeleteCharHandler());
        registerHandler(RecvOpcode.VIEW_ALL_CHAR, new ViewAllCharHandler());
        registerHandler(RecvOpcode.PICK_ALL_CHAR, new ViewAllCharSelectedHandler());
        registerHandler(RecvOpcode.REGISTER_PIN, new RegisterPinHandler());
        registerHandler(RecvOpcode.GUEST_LOGIN, new GuestLoginHandler());
        registerHandler(RecvOpcode.REGISTER_PIC, new RegisterPicHandler());
        registerHandler(RecvOpcode.CHAR_SELECT_WITH_PIC, new CharSelectedWithPicHandler());
        registerHandler(RecvOpcode.SET_GENDER, new SetGenderHandler());
        registerHandler(RecvOpcode.VIEW_ALL_WITH_PIC, new ViewAllCharSelectedWithPicHandler());
        registerHandler(RecvOpcode.VIEW_ALL_PIC_REGISTER, new ViewAllCharRegisterPicHandler());
    }

    /**
     * 注册频道服务器专用处理器
     * 处理游戏内的所有操作：移动、攻击、聊天、NPC对话、道具、技能、组队、公会等
     */
    private void registerChannelHandlers() {
        registerHandler(RecvOpcode.NAME_TRANSFER, new TransferNameHandler());
        registerHandler(RecvOpcode.CHECK_CHAR_NAME, new TransferNameResultHandler());
        registerHandler(RecvOpcode.WORLD_TRANSFER, new TransferWorldHandler());
        registerHandler(RecvOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
        registerHandler(RecvOpcode.STRANGE_DATA, LoginRequiringNoOpHandler.getInstance());
        registerHandler(RecvOpcode.GENERAL_CHAT, new GeneralChatHandler());
        registerHandler(RecvOpcode.WHISPER, new WhisperHandler());
        registerHandler(RecvOpcode.NPC_TALK, new NPCTalkHandler());
        registerHandler(RecvOpcode.NPC_TALK_MORE, new NPCMoreTalkHandler());
        registerHandler(RecvOpcode.QUEST_ACTION, new QuestActionHandler());
        registerHandler(RecvOpcode.GRENADE_EFFECT, new GrenadeEffectHandler());
        registerHandler(RecvOpcode.NPC_SHOP, new NPCShopHandler());
        registerHandler(RecvOpcode.ITEM_SORT, new InventoryMergeHandler());
        registerHandler(RecvOpcode.ITEM_MOVE, new ItemMoveHandler());
        registerHandler(RecvOpcode.MESO_DROP, new MesoDropHandler());
        registerHandler(RecvOpcode.PLAYER_LOGGEDIN, new PlayerLoggedinHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.CHANGE_MAP, new ChangeMapHandler());
        registerHandler(RecvOpcode.MOVE_LIFE, new MoveLifeHandler());
        registerHandler(RecvOpcode.CLOSE_RANGE_ATTACK, new CloseRangeDamageHandler());
        registerHandler(RecvOpcode.RANGED_ATTACK, new RangedAttackHandler());
        registerHandler(RecvOpcode.MAGIC_ATTACK, new MagicDamageHandler());
        registerHandler(RecvOpcode.TAKE_DAMAGE, new TakeDamageHandler());
        registerHandler(RecvOpcode.MOVE_PLAYER, new MovePlayerHandler());
        registerHandler(RecvOpcode.USE_CASH_ITEM, new UseCashItemHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.USE_ITEM, new UseItemHandler());
        registerHandler(RecvOpcode.USE_RETURN_SCROLL, new UseItemHandler());
        registerHandler(RecvOpcode.USE_UPGRADE_SCROLL, new ScrollHandler());
        registerHandler(RecvOpcode.USE_SUMMON_BAG, new UseSummonBagHandler());
        registerHandler(RecvOpcode.FACE_EXPRESSION, new FaceExpressionHandler());
        registerHandler(RecvOpcode.HEAL_OVER_TIME, new HealOvertimeHandler());
        registerHandler(RecvOpcode.ITEM_PICKUP, new ItemPickupHandler());
        registerHandler(RecvOpcode.CHAR_INFO_REQUEST, new CharInfoRequestHandler());
        registerHandler(RecvOpcode.SPECIAL_MOVE, new SpecialMoveHandler());
        registerHandler(RecvOpcode.USE_INNER_PORTAL, new InnerPortalHandler());
        registerHandler(RecvOpcode.CANCEL_BUFF, new CancelBuffHandler());
        registerHandler(RecvOpcode.CANCEL_ITEM_EFFECT, new CancelItemEffectHandler());
        registerHandler(RecvOpcode.PLAYER_INTERACTION, new PlayerInteractionHandler());
        registerHandler(RecvOpcode.RPS_ACTION, new RPSActionHandler());
        registerHandler(RecvOpcode.DISTRIBUTE_AP, new DistributeAPHandler());
        registerHandler(RecvOpcode.DISTRIBUTE_SP, new DistributeSPHandler());
        registerHandler(RecvOpcode.CHANGE_KEYMAP, new KeymapChangeHandler());
        registerHandler(RecvOpcode.CHANGE_MAP_SPECIAL, new ChangeMapSpecialHandler());
        registerHandler(RecvOpcode.STORAGE, new StorageHandler());
        registerHandler(RecvOpcode.GIVE_FAME, new GiveFameHandler());
        registerHandler(RecvOpcode.PARTY_OPERATION, new PartyOperationHandler());
        registerHandler(RecvOpcode.DENY_PARTY_REQUEST, new DenyPartyRequestHandler());
        registerHandler(RecvOpcode.MULTI_CHAT, new MultiChatHandler());
        registerHandler(RecvOpcode.USE_DOOR, new DoorHandler());
        registerHandler(RecvOpcode.ENTER_MTS, new EnterMTSHandler());
        registerHandler(RecvOpcode.ENTER_CASHSHOP, new EnterCashShopHandler());
        registerHandler(RecvOpcode.DAMAGE_SUMMON, new DamageSummonHandler());
        registerHandler(RecvOpcode.MOVE_SUMMON, new MoveSummonHandler());
        registerHandler(RecvOpcode.SUMMON_ATTACK, new SummonDamageHandler());
        registerHandler(RecvOpcode.BUDDYLIST_MODIFY, new BuddylistModifyHandler());
        registerHandler(RecvOpcode.USE_ITEMEFFECT, new UseItemEffectHandler());
        registerHandler(RecvOpcode.USE_CHAIR, new UseChairHandler());
        registerHandler(RecvOpcode.CANCEL_CHAIR, new CancelChairHandler());
        registerHandler(RecvOpcode.DAMAGE_REACTOR, new ReactorHitHandler());
        registerHandler(RecvOpcode.GUILD_OPERATION, new GuildOperationHandler());
        registerHandler(RecvOpcode.DENY_GUILD_REQUEST, new DenyGuildRequestHandler());
        registerHandler(RecvOpcode.BBS_OPERATION, new BBSOperationHandler());
        registerHandler(RecvOpcode.SKILL_EFFECT, new SkillEffectHandler());
        registerHandler(RecvOpcode.MESSENGER, new MessengerHandler());
        registerHandler(RecvOpcode.NPC_ACTION, new NPCAnimationHandler());
        registerHandler(RecvOpcode.CHECK_CASH, new TouchingCashShopHandler());
        registerHandler(RecvOpcode.CASHSHOP_OPERATION, new CashOperationHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.COUPON_CODE, new CouponCodeHandler());
        registerHandler(RecvOpcode.SPAWN_PET, new SpawnPetHandler());
        registerHandler(RecvOpcode.MOVE_PET, new MovePetHandler());
        registerHandler(RecvOpcode.PET_CHAT, new PetChatHandler());
        registerHandler(RecvOpcode.PET_COMMAND, new PetCommandHandler());
        registerHandler(RecvOpcode.PET_FOOD, new PetFoodHandler());
        registerHandler(RecvOpcode.PET_LOOT, new PetLootHandler());
        registerHandler(RecvOpcode.AUTO_AGGRO, new AutoAggroHandler());
        registerHandler(RecvOpcode.MONSTER_BOMB, new MonsterBombHandler());
        registerHandler(RecvOpcode.CANCEL_DEBUFF, new CancelDebuffHandler());
        registerHandler(RecvOpcode.USE_SKILL_BOOK, new SkillBookHandler());
        registerHandler(RecvOpcode.SKILL_MACRO, new SkillMacroHandler());
        registerHandler(RecvOpcode.NOTE_ACTION, new NoteActionHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.CLOSE_CHALKBOARD, new CloseChalkboardHandler());
        registerHandler(RecvOpcode.USE_MOUNT_FOOD, new UseMountFoodHandler());
        registerHandler(RecvOpcode.MTS_OPERATION, new MTSHandler());
        registerHandler(RecvOpcode.RING_ACTION, new RingActionHandler(channelDeps.noteService()));
        registerHandler(RecvOpcode.SPOUSE_CHAT, new SpouseChatHandler());
        registerHandler(RecvOpcode.PET_AUTO_POT, new PetAutoPotHandler());
        registerHandler(RecvOpcode.PET_EXCLUDE_ITEMS, new PetExcludeItemsHandler());
        registerHandler(RecvOpcode.OWL_ACTION, new UseOwlOfMinervaHandler());
        registerHandler(RecvOpcode.OWL_WARP, new OwlWarpHandler());
        registerHandler(RecvOpcode.TOUCH_MONSTER_ATTACK, new TouchMonsterDamageHandler());
        registerHandler(RecvOpcode.TROCK_ADD_MAP, new TrockAddMapHandler());
        registerHandler(RecvOpcode.HIRED_MERCHANT_REQUEST, new HiredMerchantRequest());
        registerHandler(RecvOpcode.MOB_BANISH_PLAYER, new MobBanishPlayerHandler());
        registerHandler(RecvOpcode.MOB_DAMAGE_MOB, new MobDamageMobHandler());
        registerHandler(RecvOpcode.REPORT, new ReportHandler());
        registerHandler(RecvOpcode.MONSTER_BOOK_COVER, new MonsterBookCoverHandler());
        registerHandler(RecvOpcode.AUTO_DISTRIBUTE_AP, new AutoAssignHandler());
        registerHandler(RecvOpcode.MAKER_SKILL, new MakerSkillHandler());
        registerHandler(RecvOpcode.USE_TREASUER_CHEST, new UseTreasureChestHandler());
        registerHandler(RecvOpcode.OPEN_FAMILY_PEDIGREE, new OpenFamilyPedigreeHandler());
        registerHandler(RecvOpcode.OPEN_FAMILY, new OpenFamilyHandler());
        registerHandler(RecvOpcode.ADD_FAMILY, new FamilyAddHandler());
        registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_SENIOR, new FamilySeparateHandler());
        registerHandler(RecvOpcode.SEPARATE_FAMILY_BY_JUNIOR, new FamilySeparateHandler());
        registerHandler(RecvOpcode.USE_FAMILY, new FamilyUseHandler());
        registerHandler(RecvOpcode.CHANGE_FAMILY_MESSAGE, new FamilyPreceptsHandler());
        registerHandler(RecvOpcode.FAMILY_SUMMON_RESPONSE, new FamilySummonResponseHandler());
        registerHandler(RecvOpcode.USE_HAMMER, new UseHammerHandler());
        registerHandler(RecvOpcode.SCRIPTED_ITEM, new ScriptedItemHandler());
        registerHandler(RecvOpcode.TOUCHING_REACTOR, new TouchReactorHandler());
        registerHandler(RecvOpcode.BEHOLDER, new BeholderHandler());
        registerHandler(RecvOpcode.ADMIN_COMMAND, new AdminCommandHandler());
        registerHandler(RecvOpcode.ADMIN_LOG, new AdminLogHandler());
        registerHandler(RecvOpcode.ALLIANCE_OPERATION, new AllianceOperationHandler());
        registerHandler(RecvOpcode.DENY_ALLIANCE_REQUEST, new DenyAllianceRequestHandler());
        registerHandler(RecvOpcode.USE_SOLOMON_ITEM, new UseSolomonHandler());
        registerHandler(RecvOpcode.USE_GACHA_EXP, new UseGachaExpHandler());
        registerHandler(RecvOpcode.NEW_YEAR_CARD_REQUEST, new NewYearCardHandler());
        registerHandler(RecvOpcode.CASHSHOP_SURPRISE, new CashShopSurpriseHandler());
        registerHandler(RecvOpcode.USE_ITEM_REWARD, new ItemRewardHandler());
        registerHandler(RecvOpcode.USE_REMOTE, new RemoteGachaponHandler());
        registerHandler(RecvOpcode.ACCEPT_FAMILY, new AcceptFamilyHandler());
        registerHandler(RecvOpcode.DUEY_ACTION, new DueyHandler());
        registerHandler(RecvOpcode.USE_DEATHITEM, new UseDeathItemHandler());
        registerHandler(RecvOpcode.PLAYER_MAP_TRANSFER, new PlayerMapTransitionHandler());
        registerHandler(RecvOpcode.USE_MAPLELIFE, new UseMapleLifeHandler());
        registerHandler(RecvOpcode.USE_CATCH_ITEM, new UseCatchItemHandler());
        registerHandler(RecvOpcode.FIELD_DAMAGE_MOB, new FieldDamageMobHandler());
        registerHandler(RecvOpcode.MOB_DAMAGE_MOB_FRIENDLY, new MobDamageMobFriendlyHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_REGISTER, new PartySearchRegisterHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_START, new PartySearchStartHandler());
        registerHandler(RecvOpcode.PARTY_SEARCH_UPDATE, new PartySearchUpdateHandler());
        registerHandler(RecvOpcode.ITEM_SORT2, new InventorySortHandler());
        registerHandler(RecvOpcode.LEFT_KNOCKBACK, new LeftKnockbackHandler());
        registerHandler(RecvOpcode.SNOWBALL, new SnowballHandler());
        registerHandler(RecvOpcode.COCONUT, new CoconutHandler());
        registerHandler(RecvOpcode.ARAN_COMBO_COUNTER, new AranComboHandler());
        registerHandler(RecvOpcode.CLICK_GUIDE, new ClickGuideHandler());
        registerHandler(RecvOpcode.FREDRICK_ACTION, new FredrickHandler(channelDeps.fredrickProcessor()));
        registerHandler(RecvOpcode.MONSTER_CARNIVAL, new MonsterCarnivalHandler());
        registerHandler(RecvOpcode.REMOTE_STORE, new RemoteStoreHandler());
        registerHandler(RecvOpcode.WEDDING_ACTION, new WeddingHandler());
        registerHandler(RecvOpcode.WEDDING_TALK, new WeddingTalkHandler());
        registerHandler(RecvOpcode.WEDDING_TALK_MORE, new WeddingTalkMoreHandler());
        registerHandler(RecvOpcode.WATER_OF_LIFE, new UseWaterOfLifeHandler());
        registerHandler(RecvOpcode.ADMIN_CHAT, new AdminChatHandler());
        registerHandler(RecvOpcode.MOVE_DRAGON, new MoveDragonHandler());
        registerHandler(RecvOpcode.OPEN_ITEMUI, new RaiseUIStateHandler());
        registerHandler(RecvOpcode.USE_ITEMUI, new RaiseIncExpHandler());
        registerHandler(RecvOpcode.CHANGE_QUICKSLOT, new QuickslotKeyMappedModifiedHandler());
        registerHandler(RecvOpcode.SET_HPMPALERT, new SetHpMpAlertHandler());
    }
}
