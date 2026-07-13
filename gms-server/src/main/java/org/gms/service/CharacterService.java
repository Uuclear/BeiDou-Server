package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.*;
import org.gms.client.Character;
import org.gms.client.keybind.KeyBinding;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.MapId;
import org.gms.constants.string.ExtendType;
import org.gms.dao.entity.*;
import org.gms.dao.mapper.*;
import org.gms.model.dto.CharacterListItemDTO;
import org.gms.model.dto.ChrOnlineListReqDTO;
import org.gms.model.dto.ChrOnlineListRtnDTO;
import org.gms.exception.BizException;
import org.gms.model.pojo.SkillEntry;
import org.gms.net.server.Server;
import org.gms.net.server.coordinator.session.SessionCoordinator;
import org.gms.net.server.guild.GuildCharacter;
import org.gms.net.server.world.Messenger;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.net.server.world.World;
import org.gms.server.Storage;
import org.gms.server.life.MobSkill;
import org.gms.server.life.MobSkillFactory;
import org.gms.server.life.MobSkillType;
import org.gms.server.maps.*;
import org.gms.util.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static com.mybatisflex.core.query.QueryMethods.dateDiff;
import static com.mybatisflex.core.query.QueryMethods.now;
import static org.gms.dao.entity.table.AccountsDOTableDef.ACCOUNTS_D_O;
import static org.gms.dao.entity.table.AreaInfoDOTableDef.AREA_INFO_D_O;
import static org.gms.dao.entity.table.BbsRepliesDOTableDef.BBS_REPLIES_D_O;
import static org.gms.dao.entity.table.BbsThreadsDOTableDef.BBS_THREADS_D_O;
import static org.gms.dao.entity.table.BuddiesDOTableDef.BUDDIES_D_O;
import static org.gms.dao.entity.table.CharactersDOTableDef.CHARACTERS_D_O;
import static org.gms.dao.entity.table.CooldownsDOTableDef.COOLDOWNS_D_O;
import static org.gms.dao.entity.table.EventstatsDOTableDef.EVENTSTATS_D_O;
import static org.gms.dao.entity.table.ExtendValueDOTableDef.EXTEND_VALUE_D_O;
import static org.gms.dao.entity.table.FamelogDOTableDef.FAMELOG_D_O;
import static org.gms.dao.entity.table.FamilyCharacterDOTableDef.FAMILY_CHARACTER_D_O;
import static org.gms.dao.entity.table.FredstorageDOTableDef.FREDSTORAGE_D_O;
import static org.gms.dao.entity.table.KeymapDOTableDef.KEYMAP_D_O;
import static org.gms.dao.entity.table.MonsterbookDOTableDef.MONSTERBOOK_D_O;
import static org.gms.dao.entity.table.PlayerdiseasesDOTableDef.PLAYERDISEASES_D_O;
import static org.gms.dao.entity.table.SavedlocationsDOTableDef.SAVEDLOCATIONS_D_O;
import static org.gms.dao.entity.table.ServerQueueDOTableDef.SERVER_QUEUE_D_O;
import static org.gms.dao.entity.table.SkillmacrosDOTableDef.SKILLMACROS_D_O;
import static org.gms.dao.entity.table.SkillsDOTableDef.SKILLS_D_O;
import static org.gms.dao.entity.table.TrocklocationsDOTableDef.TROCKLOCATIONS_D_O;
import static org.gms.dao.entity.table.WishlistsDOTableDef.WISHLISTS_D_O;

/**
 * 角色服务类
 * 提供角色相关的核心业务逻辑，包括角色CRUD、在线角色管理、角色数据加载/保存、
 * 角色扩展值（经验/掉落/金币倍率）管理、排行榜查询、公会管理、账号级联删除等功能。
 *
 * @author GMS Server
 * @since 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class CharacterService {
    /** 扩展值数据访问接口 */
    private final ExtendValueMapper extendValueMapper;
    /** 角色数据访问接口 */
    private final CharactersMapper charactersMapper;
    /** 技能数据访问接口 */
    private final SkillsMapper skillsMapper;
    /** 技能宏数据访问接口 */
    private final SkillmacrosMapper skillmacrosMapper;
    /** 公会数据访问接口 */
    private final GuildsMapper guildsMapper;
    /** 好友数据访问接口 */
    private final BuddiesMapper buddiesMapper;
    /** BBS帖子数据访问接口 */
    private final BbsThreadsMapper bbsThreadsMapper;
    /** BBS回复数据访问接口 */
    private final BbsRepliesMapper bbsRepliesMapper;
    /** 愿望单数据访问接口 */
    private final WishlistsMapper wishlistsMapper;
    /** 技能冷却数据访问接口 */
    private final CooldownsMapper cooldownsMapper;
    /** 玩家状态异常数据访问接口 */
    private final PlayerdiseasesMapper playerdiseasesMapper;
    /** 区域信息数据访问接口 */
    private final AreaInfoMapper areaInfoMapper;
    /** 怪物卡数据访问接口 */
    private final MonsterbookMapper monsterbookMapper;
    /** 家族成员数据访问接口 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 声望日志数据访问接口 */
    private final FamelogMapper famelogMapper;
    /** 背包服务 */
    private final InventoryService inventoryService;
    /** 任务服务 */
    private final QuestService questService;
    /** 弗雷德仓库数据访问接口 */
    private final FredstorageMapper fredstorageMapper;
    /** 拍卖行服务 */
    private final MtsService mtsService;
    /** 按键映射数据访问接口 */
    private final KeymapMapper keymapMapper;
    /** 保存位置数据访问接口 */
    private final SavedlocationsMapper savedlocationsMapper;
    /** 瞬移岩石位置数据访问接口 */
    private final TrocklocationsMapper trocklocationsMapper;
    /** 事件统计数据访问接口 */
    private final EventstatsMapper eventstatsMapper;
    /** 服务器队列数据访问接口 */
    private final ServerQueueMapper serverQueueMapper;
    /** 改名服务 */
    private final NameChangeService nameChangeService;
    /** 世界转移服务 */
    private final WorldTransferService worldTransferService;
    /** 每日BOSS日志数据访问接口 */
    private final BosslogDailyMapper bosslogDailyMapper;
    /** 每周BOSS日志数据访问接口 */
    private final BosslogWeeklyMapper bosslogWeeklyMapper;
    /** 家族权限数据访问接口 */
    private final FamilyEntitlementMapper familyEntitlementMapper;
    /** 商人背包数据访问接口 */
    private final InventorymerchantMapper inventorymerchantMapper;
    /** Spring应用上下文，用于获取代理对象保证事务生效 */
    private final ApplicationContext applicationContext;
    /** 账号数据访问接口 */
    private final AccountsMapper accountsMapper;
    /** 快捷栏按键映射数据访问接口 */
    private final QuickslotkeymappedMapper quickslotkeymappedMapper;
    /** 仓库数据访问接口 */
    private final StoragesMapper storagesMapper;
    /** 背包物品数据访问接口 */
    private final InventoryitemsMapper inventoryitemsMapper;
    /** HWID账号关联数据访问接口 */
    private final HwidaccountsMapper hwidaccountsMapper;
    /** IP封禁数据访问接口 */
    private final IpbansMapper ipbansMapper;
    /** MAC封禁数据访问接口 */
    private final MacbansMapper macbansMapper;

    /**
     * 根据角色ID查询角色信息
     *
     * @param id 角色ID
     * @return 角色数据对象，不存在则返回null
     */
    public CharactersDO findById(int id) {
        return charactersMapper.selectOneById(id);
    }

    /**
     * 更新角色信息
     *
     * @param condition 包含更新字段的角色数据对象
     */
    public void update(CharactersDO condition) {
        charactersMapper.update(condition);
    }

    /**
     * 获取在线角色列表（分页）
     * 根据世界、角色ID、角色名、地图ID进行过滤
     *
     * @param request 查询条件，包含世界ID、角色ID、角色名、地图ID、分页参数
     * @return 分页后的在线角色列表
     */
    public Page<ChrOnlineListRtnDTO> getChrOnlineList(ChrOnlineListReqDTO request) {
        Collection<Character> chrList = Server.getInstance().getWorld(request.getWorld()).getPlayerStorage().getAllCharacters();
        return BasePageUtil.create(chrList, request)
                .filter(chr -> (Objects.isNull(request.getId()) || Objects.equals(chr.getId(), request.getId()))
                        && (RequireUtil.isEmpty(request.getName()) || chr.getName().contains(request.getName()))
                        && (Objects.isNull(request.getMap()) || Objects.equals(chr.getMap().getId(), request.getMap())))
                .page(chr -> ChrOnlineListRtnDTO.builder()
                        .id(chr.getId())
                        .name(chr.getName())
                        .map(chr.getMap().getId())
                        .job(chr.getJob().getId())
                        .jobName(chr.getJob().getName())
                        .level(chr.getLevel())
                        .gm(chr.gmLevel())
                        .build());
    }

    /**
     * 更新角色倍率（经验/掉落/金币）
     * 更新扩展值表，并重置在线角色的倍率设置
     *
     * @param data 扩展值数据，包含角色ID、扩展类型、扩展名称、扩展值
     */
    public void updateRate(ExtendValueDO data) {
        checkName(data);
        data.setExtendType(ExtendType.CHARACTER_EXTEND.getType());
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(data.getExtendId(), data.getExtendType(), data.getExtendName());
        if (extendValueDO == null) {
            extendValueMapper.insertSelective(data);
        } else {
            data.setCreateTime(null);
            data.setUpdateTime(new Date(System.currentTimeMillis()));
            extendValueMapper.update(data);
        }

        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置单项角色倍率
     * 删除指定的扩展值记录，并重置在线角色的倍率设置
     *
     * @param data 扩展值数据，包含角色ID、扩展类型、扩展名称
     */
    public void resetRate(ExtendValueDO data) {
        checkName(data);
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(data.getExtendId()))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(ExtendType.CHARACTER_EXTEND.getType()))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.eq(data.getExtendName())));
        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置所有角色倍率（经验/掉落/金币）
     * 删除角色的三项倍率扩展值，并重置在线角色的倍率设置
     *
     * @param data 扩展值数据，包含角色ID、扩展类型
     */
    public void resetRates(ExtendValueDO data) {
        check(data);
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(data.getExtendId()))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.eq(ExtendType.CHARACTER_EXTEND.getType()))
                .and(EXTEND_VALUE_D_O.EXTEND_NAME.in("expRate", "dropRate", "mesoRate")));
        Character character = getCharacter(data);
        character.resetPlayerRates();
        character.setWorldRates();
        character.setCouponRates();
    }

    /**
     * 重置所有角色的商人状态
     * 将所有角色的hasMerchant字段设置为0，用于服务器重启时清理异常状态
     */
    public void resetMerchant() {
        charactersMapper.updateAllHasMerchant(0);
    }

    /**
     * 获取世界排行榜玩家列表
     * 支持全服排行或按世界分别排行，查询非GM、未封禁角色的前50名
     *
     * @param worldSize 世界数量
     * @return 排行榜列表，全服排行时外层列表只有一个元素
     */
    public List<List<CharactersDO>> getWorldsRankPlayers(int worldSize) {
        boolean wholeServerRanking = GameConfig.getServerBoolean("use_whole_server_ranking");
        List<List<CharactersDO>> worldsRankingList = new ArrayList<>();
        if (wholeServerRanking) {
            // 全服前50
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .select(CHARACTERS_D_O.NAME, CHARACTERS_D_O.LEVEL, CHARACTERS_D_O.WORLD)
                    .from(CHARACTERS_D_O)
                    .leftJoin(ACCOUNTS_D_O).on(CHARACTERS_D_O.ACCOUNTID.eq(ACCOUNTS_D_O.ID))
                    .where(CHARACTERS_D_O.GM.lt(2))
                    .and(ACCOUNTS_D_O.BANNED.eq(0).or(ACCOUNTS_D_O.TEMPBAN.isNull()))
                    .and(CHARACTERS_D_O.WORLD.between(0, worldSize - 1))
                    .orderBy(CHARACTERS_D_O.WORLD.asc(), CHARACTERS_D_O.LEVEL.desc(), CHARACTERS_D_O.EXP.desc(), CHARACTERS_D_O.LAST_EXP_GAIN_TIME.asc())
                    .limit(50);
            List<CharactersDO> charactersDOList = charactersMapper.selectListByQuery(queryWrapper);
            worldsRankingList.add(charactersDOList);
        } else {
            for (int i = 0; i < worldSize; i++) {
                // 每个区前50
                List<CharactersDO> charactersDOList = getWorldRankPlayers(i);
                worldsRankingList.add(charactersDOList);
            }
        }
        return worldsRankingList;
    }

    /**
     * 获取单个世界的排行榜玩家列表
     *
     * @param worldId 世界ID
     * @return 该世界前50名角色列表
     */
    public List<CharactersDO> getWorldRankPlayers(int worldId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(CHARACTERS_D_O.NAME, CHARACTERS_D_O.LEVEL, CHARACTERS_D_O.WORLD)
                .from(CHARACTERS_D_O)
                .leftJoin(ACCOUNTS_D_O).on(CHARACTERS_D_O.ACCOUNTID.eq(ACCOUNTS_D_O.ID))
                .where(CHARACTERS_D_O.GM.lt(2))
                .and(ACCOUNTS_D_O.BANNED.eq(0).or(ACCOUNTS_D_O.TEMPBAN.isNull()))
                .and(CHARACTERS_D_O.WORLD.eq(worldId))
                .orderBy(CHARACTERS_D_O.LEVEL.desc(), CHARACTERS_D_O.EXP.desc(), CHARACTERS_D_O.LAST_EXP_GAIN_TIME.asc())
                .limit(50);
        return charactersMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据角色名查询角色信息
     *
     * @param name 角色名
     * @return 角色数据对象，不存在则返回null
     */
    public CharactersDO findByName(String name) {
        List<CharactersDO> charactersDOS = charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.NAME.eq(name)));
        return charactersDOS.isEmpty() ? null : charactersDOS.getFirst();
    }

    /**
     * 删除角色技能
     *
     * @param skillsDO 技能数据对象，包含角色ID和技能ID
     */
    public void removeSkill(SkillsDO skillsDO) {
        skillsMapper.deleteByQuery(QueryWrapper.create(skillsDO));
    }

    /**
     * 删除公会
     * 将会内所有角色的公会ID置0，公会等级置为5（会员），然后删除公会记录
     *
     * @param guildsDO 公会数据对象，包含公会ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGuild(GuildsDO guildsDO) {
        charactersMapper.updateByQuery(CharactersDO.builder().guildid(0).guildrank(5).build(), QueryWrapper.create().where(CHARACTERS_D_O.GUILDID.eq(guildsDO.getGuildid())));
        guildsMapper.deleteById(guildsDO.getGuildid());
    }

    /**
     * 从数据库删除角色（带鉴权）
     * 验证发送者账号是否拥有该角色，然后执行删除
     *
     * @param player 要删除的在线角色对象
     * @param senderAccId 发送者账号ID
     * @throws BizException 角色不属于该账号时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCharFromDB(Character player, int senderAccId) {
        int cid = player.getId();
        if (!Server.getInstance().haveCharacterEntry(senderAccId, cid)) {    // thanks zera (EpiphanyMS) for pointing a critical exploit with non-authed character deletion request
            throw new BizException(I18nUtil.getExceptionMessage("UNKNOWN_CHARACTER"));
        }
        deleteCharacterById(cid);
    }

    /**
     * 按角色ID删除角色及其全部关联数据（GM后台/账号级联删除入口，无登录态鉴权）。
     * 不依赖在线 Character 对象，guild 清理传 null character（仅 leave/disband，跳过 setGuildMemberOnline）。
     *
     * @param cid 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCharacterById(int cid) {
        CharactersDO charactersDO = findById(cid);
        if (charactersDO == null) {
            return;
        }
        int world = charactersDO.getWorld();
        // 删除guild（传 null character：跳过 setGuildMemberOnline，仍执行 leaveGuild/disbandGuild）
        if (Optional.ofNullable(charactersDO.getGuildid()).orElse(0) > 0) {
            Server.getInstance().deleteGuildCharacter(new GuildCharacter(null, cid, 0, charactersDO.getName(),
                    (byte) -1, (byte) -1, 0, Optional.ofNullable(charactersDO.getGuildrank()).orElse(0),
                    Optional.ofNullable(charactersDO.getGuildid()).orElse(0), false,
                    Optional.ofNullable(charactersDO.getAllianceRank()).orElse(0)));
        }
        // 删除buddies
        QueryWrapper buddiesQueryWrapper = QueryWrapper.create().where(BUDDIES_D_O.CHARACTERID.eq(cid));
        List<BuddiesDO> buddiesDOS = buddiesMapper.selectListByQuery(buddiesQueryWrapper);
        buddiesDOS.forEach(buddiesDO -> {
            Character buddy = Server.getInstance().getWorld(world).getPlayerStorage().getCharacterById(buddiesDO.getBuddyid());
            if (buddy != null) {
                buddy.deleteBuddy(cid);
            }
        });
        buddiesMapper.deleteByQuery(buddiesQueryWrapper);
        // 删除bbs_threads bbs_replies
        QueryWrapper bbsThreadsQueryWrapper = QueryWrapper.create().where(BBS_THREADS_D_O.POSTERCID.eq(cid));
        List<BbsThreadsDO> bbsThreadsDOS = bbsThreadsMapper.selectListByQuery(bbsThreadsQueryWrapper);
        List<Long> threadIds = bbsThreadsDOS.stream().map(BbsThreadsDO::getThreadid).toList();
        if (!threadIds.isEmpty()) {
            bbsRepliesMapper.deleteByQuery(QueryWrapper.create().where(BBS_REPLIES_D_O.THREADID.in(threadIds)));
            bbsThreadsMapper.deleteByQuery(bbsThreadsQueryWrapper);
        }
        // 删除wishlists
        wishlistsMapper.deleteByQuery(QueryWrapper.create().where(WISHLISTS_D_O.CHARID.eq(cid)));
        // 删除cooldowns
        cooldownsMapper.deleteByQuery(QueryWrapper.create().where(COOLDOWNS_D_O.CHARID.eq(cid)));
        // 删除playerdiseases
        playerdiseasesMapper.deleteByQuery(QueryWrapper.create().where(PLAYERDISEASES_D_O.CHARID.eq(cid)));
        // 删除area_info
        areaInfoMapper.deleteByQuery(QueryWrapper.create().where(AREA_INFO_D_O.CHARID.eq(cid)));
        // 删除monsterbook
        monsterbookMapper.deleteByQuery(QueryWrapper.create().where(MONSTERBOOK_D_O.CHARID.eq(cid)));
        // 删除characters
        charactersMapper.deleteById(cid);
        // 删除family_character
        familyCharacterMapper.deleteByQuery(QueryWrapper.create().where(FAMILY_CHARACTER_D_O.CID.eq(cid)));
        // 删除famelog
        famelogMapper.deleteByQuery(QueryWrapper.create().where(FAMELOG_D_O.CHARACTERID_TO.eq(cid).or(FAMELOG_D_O.CHARACTERID.eq(cid))));
        // 删除背包库存
        inventoryService.deleteInventoryByCharacterId(cid);
        // 删除任务进度
        questService.deleteQuestProgressByCharacter(cid);
        // 删除fredstorage
        fredstorageMapper.deleteByQuery(QueryWrapper.create().where(FREDSTORAGE_D_O.CID.eq(cid)));
        // 删除拍卖行
        mtsService.deleteMtsByCharacterId(cid);
        // 删除keymap
        keymapMapper.deleteByQuery(QueryWrapper.create().where(KEYMAP_D_O.CHARACTERID.eq(cid)));
        // 删除savedlocations
        savedlocationsMapper.deleteByQuery(QueryWrapper.create().where(SAVEDLOCATIONS_D_O.CHARACTERID.eq(cid)));
        // 删除trocklocations
        trocklocationsMapper.deleteByQuery(QueryWrapper.create().where(TROCKLOCATIONS_D_O.CHARACTERID.eq(cid)));
        // 删除技能
        skillsMapper.deleteByQuery(QueryWrapper.create().where(SKILLS_D_O.CHARACTERID.eq(cid)));
        skillmacrosMapper.deleteByQuery(QueryWrapper.create().where(SKILLMACROS_D_O.CHARACTERID.eq(cid)));
        // 删除eventstats
        eventstatsMapper.deleteByQuery(QueryWrapper.create().where(EVENTSTATS_D_O.CHARACTERID.eq(cid)));
        // 删除server_queue
        serverQueueMapper.deleteByQuery(QueryWrapper.create().where(SERVER_QUEUE_D_O.CHARACTERID.eq(cid)));
        // 删除bosslog
        bosslogDailyMapper.deleteByQuery(new QueryWrapper().eq("characterid", cid));
        bosslogWeeklyMapper.deleteByQuery(new QueryWrapper().eq("characterid", cid));
        // 删除family_entitlement
        familyEntitlementMapper.deleteByQuery(new QueryWrapper().eq("charid", cid));
        // 删除inventorymerchant
        inventorymerchantMapper.deleteByQuery(new QueryWrapper().eq("characterid", cid));
        // 删除character_extend系列（角色扩展值，复用统一扩展表）
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(String.valueOf(cid)))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.in(
                        ExtendType.CHARACTER_EXTEND.getType(),
                        ExtendType.CHARACTER_EXTEND_DAILY.getType(),
                        ExtendType.CHARACTER_EXTEND_WEEKLY.getType())));
        // 删除characterexplogs（经验日志，服务端由ExpLogger写入，无Mapper用原生JDBC）
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM characterexplogs WHERE charid = ?")) {
            ps.setInt(1, cid);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("删除 characterexplogs 失败, cid={}", cid, e);
        }
        // 补充heaven没有删除的2张表
        nameChangeService.cancelPendingNameChange(cid, false);
        worldTransferService.cancelPendingWorldTransfer(cid, false);
    }

    /**
     * 保存新角色到数据库
     *
     * @param player 角色对象
     * @param notAutosave 是否为非自动保存（用于日志区分）
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public void saveCharToDB(Character player, boolean notAutosave) {
        if (!player.isLoggedIn()) {
            return;
        }
        log.info(I18nUtil.getLogMessage(notAutosave ? "Character.saveCharToDB.info1" : "Character.saveCharToDB.info2"), player.getName());
        Server.getInstance().updateCharacterEntry(player);

        CharactersDO cdo = Character.toCharactersDO(player);
        charactersMapper.insertSelective(cdo);
    }

    /**
     * 从数据库加载角色数据
     * 包括角色基础信息、地图位置、组队、Messenger、任务状态、技能、冷却、状态异常、
     * 技能宏、按键映射、保存位置、声望记录、好友列表、仓库等完整数据
     *
     * @param cid 角色ID
     * @param client 客户端连接
     * @param channelServer 是否为频道服务器加载（频道服务器需要加载完整地图和在线数据）
     * @return 加载完成的角色对象
     * @throws BizException 角色不存在时抛出异常
     */
    public Character loadCharFromDB(int cid, Client client, boolean channelServer) {
        CharactersDO charactersDO = findById(cid);
        RequireUtil.requireNotNull(charactersDO, I18nUtil.getExceptionMessage("UNKNOWN_CHARACTER"));
        Character chr = Character.fromCharactersDO(charactersDO, client);
        if (!channelServer) {
            return chr;
        }
        MapManager mapManager = client.getChannelServer().getMapFactory();
        MapleMap mapleMap = mapManager.getMap(chr.getMapId());
        if (mapleMap == null) {
            mapleMap = mapManager.getMap(MapId.HENESYS);
        }
        chr.setMap(mapleMap);
        Portal portal = mapleMap.getPortal(chr.getInitialSpawnPoint());
        if (portal == null) {
            portal = mapleMap.getPortal(0);
            chr.setInitialSpawnPoint(0);
        }
        chr.setPosition(portal.getPosition());

        World world = Server.getInstance().getWorld(charactersDO.getWorld());
        int partyId = charactersDO.getParty();
        Party party = world.getParty(partyId);
        if (party != null) {
            PartyCharacter partyCharacter = party.getMemberById(cid);
            if (partyCharacter != null) {
                chr.setMPC(new PartyCharacter(chr));
                chr.setParty(party);
            }
        }

        int messengerId = charactersDO.getMessengerid();
        int messengerPosition = charactersDO.getMessengerposition();
        if (messengerId > 0 && messengerPosition < 4 && messengerPosition > -1) {
            Messenger messenger = world.getMessenger(messengerId);
            if (messenger != null) {
                chr.setMessenger(messenger);
                chr.setMessengerPosition(messengerPosition);
            }
        }
        chr.setLoggedIn(true);

        List<QuestStatus> questStatusList = questService.getQuestStatusByCharacter(cid);
        questStatusList.forEach(questStatus -> chr.getQuests().put(questStatus.getQuestID(), questStatus));

        List<SkillsDO> skillsDOList = skillsMapper.selectListByQuery(QueryWrapper.create().where(SKILLS_D_O.CHARACTERID.eq(cid)));
        skillsDOList.forEach(skillsDO -> {
            Skill skill = SkillFactory.getSkill(skillsDO.getSkillid());
            if (skill != null) {
                chr.getEditableSkills().put(skill, new SkillEntry(Optional.ofNullable(skillsDO.getSkilllevel()).map(Integer::byteValue).orElse((byte) 0),
                        skillsDO.getMasterlevel(), skillsDO.getExpiration()));
            }
        });

        QueryWrapper cdQueryWrapper = QueryWrapper.create().where(COOLDOWNS_D_O.CHARID.eq(cid));
        List<CooldownsDO> cooldownsDOList = cooldownsMapper.selectListByQuery(cdQueryWrapper);
        cooldownsDOList.forEach(cooldownsDO -> {
            if (cooldownsDO.getSkillid() != 5221999 && cooldownsDO.getLength() + cooldownsDO.getStarttime() < System.currentTimeMillis()) {
                return;
            }
            chr.giveCoolDowns(cooldownsDO.getSkillid(), cooldownsDO.getStarttime(), cooldownsDO.getLength());
        });
        cooldownsMapper.deleteByQuery(cdQueryWrapper);

        QueryWrapper pdWrapper = QueryWrapper.create().where(PLAYERDISEASES_D_O.CHARID.eq(cid));
        List<PlayerdiseasesDO> playerdiseasesDOList = playerdiseasesMapper.selectListByQuery(pdWrapper);
        Map<Disease, Pair<Long, MobSkill>> loadedDiseases = new LinkedHashMap<>();
        playerdiseasesDOList.forEach(playerdiseasesDO -> {
            Disease ordinal = Disease.ordinal(playerdiseasesDO.getDisease());
            if (Disease.NULL.equals(ordinal)) {
                return;
            }
            MobSkillType mobSkillType = MobSkillType.from(playerdiseasesDO.getMobskillid()).orElseThrow();
            MobSkill mobSkill = MobSkillFactory.getMobSkillOrThrow(mobSkillType, playerdiseasesDO.getMobskilllv());
            loadedDiseases.put(ordinal, new Pair<>(playerdiseasesDO.getLength(), mobSkill));
        });
        playerdiseasesMapper.deleteByQuery(pdWrapper);
        if (!loadedDiseases.isEmpty()) {
            Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(cid, loadedDiseases);
        }

        List<SkillmacrosDO> skillmacrosDOList = skillmacrosMapper.selectListByQuery(QueryWrapper.create().where(SKILLMACROS_D_O.CHARACTERID.eq(cid)));
        skillmacrosDOList.forEach(skillmacrosDO -> chr.getSkillMacros()[skillmacrosDO.getPosition()] = new SkillMacro(
                skillmacrosDO.getSkill1(), skillmacrosDO.getSkill2(), skillmacrosDO.getSkill3(), skillmacrosDO.getName(),
                skillmacrosDO.getShout(), skillmacrosDO.getPosition()
        ));

        List<KeymapDO> keymapDOList = keymapMapper.selectListByQuery(QueryWrapper.create().where(KEYMAP_D_O.CHARACTERID.eq(cid)));
        keymapDOList.forEach(keymapDO -> chr.getKeymap().put(keymapDO.getKey(), new KeyBinding(keymapDO.getType(), keymapDO.getAction())));

        List<SavedlocationsDO> savedlocationsDOList = savedlocationsMapper.selectListByQuery(QueryWrapper.create().where(SAVEDLOCATIONS_D_O.CHARACTERID.eq(cid)));
        savedlocationsDOList.forEach(savedlocationsDO -> chr.getSavedLocations()[SavedLocationType.valueOf(savedlocationsDO.getLocationtype()).ordinal()]
                = new SavedLocation(savedlocationsDO.getMap(), savedlocationsDO.getPortal()));

        List<FamelogDO> famelogDOList = famelogMapper.selectListByQuery(QueryWrapper.create()
                .where(FAMELOG_D_O.CHARACTERID.eq(cid)).and(dateDiff(now(), FAMELOG_D_O.WHEN).lt(30)));
        long lastFameTime = 0;
        List<Integer> lastMonthFameIds = new ArrayList<>(31);
        for (FamelogDO famelogDO : famelogDOList) {
            lastFameTime = Math.max(lastFameTime, famelogDO.getWhen().getTime());
            lastMonthFameIds.add(famelogDO.getCharacteridTo());
        }
        chr.setLastfametime(lastFameTime);
        chr.setLastmonthfameids(lastMonthFameIds);

        chr.getBuddylist().loadFromDb(cid);
        Storage accountStorage = world.getAccountStorage(charactersDO.getAccountid());
        if (accountStorage == null) {
            world.loadAccountStorage(charactersDO.getAccountid());
            accountStorage = world.getAccountStorage(charactersDO.getAccountid());
        }
        chr.setStorage(accountStorage);
        chr.reapplyLocalStats();
        chr.changeHpMp(charactersDO.getHp(), charactersDO.getMp(), true);
        return chr;
    }

    /**
     * 根据角色ID获取瞬移岩石位置列表
     *
     * @param cid 角色ID
     * @return 瞬移岩石位置列表
     */
    public List<TrocklocationsDO> getTrockLocationByCharacter(Integer cid) {
        return trocklocationsMapper.selectListByQuery(QueryWrapper.create().where(TROCKLOCATIONS_D_O.CHARACTERID.eq(cid)));
    }

    /**
     * 根据角色ID获取区域信息列表
     *
     * @param cid 角色ID
     * @return 区域信息列表
     */
    public List<AreaInfoDO> getAreaInfoByCharacter(Integer cid) {
        return areaInfoMapper.selectListByQuery(QueryWrapper.create().where(AREA_INFO_D_O.CHARID.eq(cid)));
    }

    /**
     * 根据角色ID获取事件统计列表
     *
     * @param cid 角色ID
     * @return 事件统计列表
     */
    public List<EventstatsDO> getEventStatsByCharacter(Integer cid) {
        return eventstatsMapper.selectListByQuery(QueryWrapper.create().where(EVENTSTATS_D_O.CHARACTERID.eq(cid)));
    }

    /**
     * 根据角色ID获取愿望单列表
     *
     * @param cid 角色ID
     * @return 愿望单列表
     */
    public List<WishlistsDO> getWishlistsByCharacter(Integer cid) {
        return wishlistsMapper.selectListByQuery(QueryWrapper.create().where(WISHLISTS_D_O.CHARID.eq(cid)));
    }

    /**
     * 根据账号ID获取角色列表
     *
     * @param accountId 账号ID
     * @return 角色数据对象列表
     */
    public List<CharactersDO> getCharacterByAccountId(int accountId) {
        return charactersMapper.selectListByQuery(QueryWrapper.create().where(CHARACTERS_D_O.ACCOUNTID.eq(accountId)));
    }

    /**
     * 根据账号ID获取角色列表（GM后台展示用）
     * 包含世界名称、职业名称、在线状态等额外信息
     *
     * @param accountId 账号ID
     * @return 角色列表项DTO列表
     */
    public List<CharacterListItemDTO> getCharacterListByAccountId(int accountId) {
        List<CharactersDO> list = getCharacterByAccountId(accountId);
        return list.stream().map(cdo -> {
            int worldId = Optional.ofNullable(cdo.getWorld()).orElse(0);
            String worldName = (worldId >= 0 && worldId < GameConstants.WORLD_NAMES.length)
                    ? GameConstants.WORLD_NAMES[worldId] : String.valueOf(worldId);
            Job job = Job.getById(cdo.getJob());
            return CharacterListItemDTO.builder()
                    .id(cdo.getId())
                    .name(cdo.getName())
                    .job(cdo.getJob())
                    .jobName(job == null ? "" : job.getName())
                    .level(cdo.getLevel())
                    .world(worldId)
                    .worldName(worldName)
                    .gm(cdo.getGm())
                    .meso(cdo.getMeso())
                    .fame(cdo.getFame())
                    .guildid(cdo.getGuildid())
                    .createdate(cdo.getCreatedate())
                    .lastLogoutTime(cdo.getLastLogoutTime())
                    .online(findOnlineCharacter(cdo.getId()) != null)
                    .build();
        }).toList();
    }

    /**
     * 删除角色：在线先下线再删，离线直接删。
     *
     * @param cid 角色ID
     * @throws BizException 角色不存在时抛出异常
     */
    public void deleteCharacterWithOnlineCheck(int cid) {
        int accountId = prepareCharacterOffline(cid);
        RequireUtil.requireTrue(accountId != 0, I18nUtil.getExceptionMessage("UNKNOWN_CHARACTER"));
        // 通过代理调用，保证 @Transactional 事务生效
        applicationContext.getBean(CharacterService.class).deleteCharacterById(cid);
        safeDeleteCharacterEntry(accountId, cid);
    }

    /**
     * 安全清理角色登录缓存：账号未登录时其 entry 未初始化，直接调用 deleteCharacterEntry 会 NPE，此处兜底忽略。
     *
     * @param accountId 账号ID
     * @param cid 角色ID
     */
    public void safeDeleteCharacterEntry(int accountId, int cid) {
        try {
            Server.getInstance().deleteCharacterEntry(accountId, cid);
        } catch (NullPointerException e) {
            // 账号未登录，无登录缓存可清
        }
    }

    /**
     * 删除账号：级联删除账号下所有角色及其关联数据 + 账号级关联表 + 账号本身。
     * AccountService 作为基类不依赖业务 Service，故账号删除下放到此。
     *
     * @param id 账号ID
     * @throws BizException 账号不存在时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(int id) {
        RequireUtil.requireNotNull(accountsMapper.selectOneById(id), I18nUtil.getExceptionMessage("AccountService.id.NotExist"));
        // 通过代理调用，保证角色级联删除的 @Transactional 加入本事务
        CharacterService self = applicationContext.getBean(CharacterService.class);
        // 1. 遍历账号下所有角色：在线先下线，再删除角色及其关联数据
        List<CharactersDO> charList = charactersMapper.selectIdAndWorldListByAccountId(id);
        for (CharactersDO chr : charList) {
            int cid = chr.getId();
            self.prepareCharacterOffline(cid);
            self.deleteCharacterById(cid);
            self.safeDeleteCharacterEntry(id, cid);
        }
        // 2. 删除账号级关联表
        quickslotkeymappedMapper.deleteByQuery(new QueryWrapper().eq("accountid", id));
        storagesMapper.deleteByQuery(new QueryWrapper().eq("accountid", id));
        inventoryitemsMapper.deleteByQuery(new QueryWrapper().eq("accountid", id));
        ipbansMapper.deleteByQuery(new QueryWrapper().eq("aid", id));
        macbansMapper.deleteByQuery(new QueryWrapper().eq("aid", id));
        hwidaccountsMapper.deleteByQuery(new QueryWrapper().eq("accountid", id));
        serverQueueMapper.deleteByQuery(new QueryWrapper().eq("accountid", id));
        extendValueMapper.deleteByQuery(QueryWrapper.create()
                .where(EXTEND_VALUE_D_O.EXTEND_ID.eq(String.valueOf(id)))
                .and(EXTEND_VALUE_D_O.EXTEND_TYPE.in(
                        ExtendType.ACCOUNT_EXTEND.getType(),
                        ExtendType.ACCOUNT_EXTEND_DAILY.getType(),
                        ExtendType.ACCOUNT_EXTEND_WEEKLY.getType())));
        // 3. 删除账号
        accountsMapper.deleteById(id);
    }

    /**
     * 在线角色先下线，返回角色所属 accountId（在线从内存取，离线从 DB 取；角色不存在返回 0）。
     * 供账号级联删除复用。
     *
     * @param cid 角色ID
     * @return 账号ID，角色不存在返回0
     */
    public int prepareCharacterOffline(int cid) {
        Character online = findOnlineCharacter(cid);
        if (online != null) {
            int accountId = online.getAccountId();
            // 在线：先下线
            online.getClient().forceDisconnect();
            // 这里必须用online.getClient()重新获取一遍
            if (online.getClient() != null) {
                online.getClient().closeSession();
            }
            return accountId;
        }
        CharactersDO cdo = findById(cid);
        return cdo == null ? 0 : cdo.getAccountid();
    }

    /**
     * 查找在线角色
     * 遍历所有世界的玩家存储查找指定ID的角色
     *
     * @param cid 角色ID
     * @return 在线角色对象，不在线返回null
     */
    private Character findOnlineCharacter(int cid) {
        for (World world : Server.getInstance().getWorlds()) {
            Character chr = world.getPlayerStorage().getCharacterById(cid);
            if (chr != null) {
                return chr;
            }
        }
        return null;
    }

    /**
     * 校验倍率更新请求参数
     * 只允许更新expRate、dropRate、mesoRate三种倍率
     *
     * @param data 扩展值数据
     * @throws BizException 参数不合法时抛出异常
     */
    private void checkName(ExtendValueDO data) {
        check(data);
        // 非法请求篡改其他字段
        if ("expRate".equals(data.getExtendName()) || "dropRate".equals(data.getExtendName()) || "mesoRate".equals(data.getExtendName())) {
            return;
        }
        throw BizException.illegalArgument();
    }

    /**
     * 校验扩展值基础参数
     *
     * @param data 扩展值数据
     * @throws BizException 参数为空时抛出异常
     */
    private void check(ExtendValueDO data) {
        RequireUtil.requireNotEmpty(data.getExtendId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendId"));
        RequireUtil.requireNotEmpty(data.getExtendType(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendType"));
        RequireUtil.requireNotEmpty(data.getExtendName(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "extendName"));
    }

    /**
     * 根据扩展值数据获取在线角色对象
     * 支持按账号ID或角色ID查找
     *
     * @param data 扩展值数据，包含扩展类型和扩展ID
     * @return 在线角色对象
     * @throws BizException 角色不在线时抛出异常
     */
    private Character getCharacter(ExtendValueDO data) {
        for (World world : Server.getInstance().getWorlds()) {
            for (Character character : world.getPlayerStorage().getAllCharacters()) {
                if (ExtendType.isAccount(data.getExtendType()) && Objects.equals(String.valueOf(character.getAccountId()), data.getExtendId())) {
                    return character;
                }

                if (ExtendType.isCharacter(data.getExtendType()) && Objects.equals(String.valueOf(character.getId()), data.getExtendId())) {
                    return character;
                }
            }
        }
        throw BizException.illegalArgument(I18nUtil.getExceptionMessage("CharacterService.getCharacter.exception1"));
    }
}
