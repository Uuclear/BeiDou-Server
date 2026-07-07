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
package org.gms.server.life;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.FamilyEntry;
import org.gms.client.Job;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.status.MonsterStatus;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.GameConfig;
import org.gms.constants.id.MobId;
import org.gms.constants.skills.Crusader;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.Hermit;
import org.gms.constants.skills.ILMage;
import org.gms.constants.skills.NightLord;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Priest;
import org.gms.constants.skills.Shadower;
import org.gms.constants.skills.WhiteKnight;
import org.gms.net.packet.Packet;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.world.MonsterAggroCoordinator;
import org.gms.net.server.services.task.channel.MobAnimationService;
import org.gms.net.server.services.task.channel.MobClearSkillService;
import org.gms.net.server.services.task.channel.MobStatusService;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.server.StatEffect;
import org.gms.server.TimerManager;
import org.gms.server.life.LifeFactory.BanishInfo;
import org.gms.server.loot.LootManager;
import org.gms.server.maps.AbstractAnimatedMapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Summon;
import org.gms.server.quest.medal.VeteranHunterMedal;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 怪物运行时实例。管理 HP/MP、仇恨控制器、状态异常、技能释放、掉落触发、自爆及与地图/队伍的交互。
 */
public class Monster extends AbstractLoadedLife {
    private static final Logger log = LoggerFactory.getLogger(Monster.class);

    private ChangeableStats ostats = null;  //未使用，v83 WZ 不支持可变更属性。
    private MonsterStats stats;
    private final AtomicInteger hp = new AtomicInteger(1);
    private final AtomicLong maxHpPlusHeal = new AtomicLong(1);
    private int mp;
    private WeakReference<Character> controller = new WeakReference<>(null);
    // 仇恨控制器状态：是否已拉仇恨、客户端是否已知仇恨、是否被傀儡吸引
    private boolean controllerHasAggro, controllerKnowsAboutAggro, controllerHasPuppet;
    private final Collection<MonsterListener> listeners = new LinkedList<>();
    private final EnumMap<MonsterStatus, MonsterStatusEffect> stati = new EnumMap<>(MonsterStatus.class);
    private final ArrayList<MonsterStatus> alreadyBuffed = new ArrayList<>();
    private MapleMap map;
    private int VenomMultiplier = 0;
    private boolean fake = false;
    private boolean dropsDisabled = false;
    private final Set<MobSkillId> usedSkills = new HashSet<>();
    private final Set<Integer> usedAttacks = new HashSet<>();
    private Set<Integer> calledMobOids = null;
    private WeakReference<Monster> callerMob = new WeakReference<>(null);
    private final List<Integer> stolenItems = new ArrayList<>(5);
    private int team;
    private int parentMobOid = 0;
    private int spawnEffect = 0;
    private final HashMap<Integer, AtomicLong> takenDamage = new HashMap<>();
    private ScheduledFuture<?> monsterItemDrop = null;
    private Runnable removeAfterAction = null;
    private boolean availablePuppetUpdate = true;

    private final Lock externalLock = new ReentrantLock();
    private final Lock monsterLock = new ReentrantLock(true);
    private final Lock statiLock = new ReentrantLock();
    private final Lock animationLock = new ReentrantLock();
    private final Lock aggroUpdateLock = new ReentrantLock();

    /**
     * 构造 Monster 实例。
     * @param id ID
     * @param stats stats
     */
    public Monster(int id, MonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    /**
     * 构造 Monster 实例。
     * @param monster 怪物
     */
    public Monster(Monster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    /**
     * 执行 lock、怪物 操作。
     */
    public void lockMonster() {
        externalLock.lock();
    }

    /**
     * 执行 unlock、怪物 操作。
     */
    public void unlockMonster() {
        externalLock.unlock();
    }

    private void initWithStats(MonsterStats baseStats) {
        setStance(5);
        this.stats = baseStats.copy();
        hp.set(stats.getHp());
        mp = stats.getMp();

        maxHpPlusHeal.set(hp.get());
    }

    /**
     * 设置刷新效果。
     * @param effect effect
     */
    public void setSpawnEffect(int effect) {
        spawnEffect = effect;
    }

    /**
     * 获取刷新效果。
     * @return int 类型结果
     */
    public int getSpawnEffect() {
        return spawnEffect;
    }

    /**
     * 执行 disable、掉落 操作。
     */
    public void disableDrops() {
        this.dropsDisabled = true;
    }

    /**
     * 执行 enable、掉落 操作。
     */
    public void enableDrops() {
        this.dropsDisabled = false;
    }

    /**
     * 执行 drops、Disabled 操作。
     * @return boolean 类型结果
     */
    public boolean dropsDisabled() {
        return dropsDisabled;
    }

    /**
     * 设置地图。
     * @param map 地图名称
     */
    public void setMap(MapleMap map) {
        this.map = map;
    }

    /**
     * 获取Parent、怪物、对象 ID。
     * @return int 类型结果
     */
    public int getParentMobOid() {
        return parentMobOid;
    }

    /**
     * 设置Parent、怪物、对象 ID。
     * @param parentMobId parentMobId
     */
    public void setParentMobOid(int parentMobId) {
        this.parentMobOid = parentMobId;
    }

    /**
     * 统计Available、怪物、Summons数量。
     * @param summonsSize summonsSize
     * @param skillLimit skillLimit
     * @return int 类型结果
     */
    public int countAvailableMobSummons(int summonsSize, int skillLimit) {    // limit prop for summons has another conotation, found thanks to MedicOP
        int summonsCount;

        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids != null) {
            summonsCount = calledOids.size();
        } else {
            summonsCount = 0;
        }

        return Math.min(summonsSize, skillLimit - summonsCount);
    }

    /**
     * 添加Summoned、怪物。
     * @param mob 怪物
     */
    public void addSummonedMob(Monster mob) {
        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids == null) {
            calledOids = Collections.synchronizedSet(new HashSet<>());
            this.calledMobOids = calledOids;
        }

        calledOids.add(mob.getObjectId());
        mob.setSummonerMob(this);
    }

    private void removeSummonedMob(int mobOid) {
        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids != null) {
            calledOids.remove(mobOid);
        }
    }

    private void setSummonerMob(Monster mob) {
        this.callerMob = new WeakReference<>(mob);
    }

    private void dispatchClearSummons() {
        Monster caller = this.callerMob.get();
        if (caller != null) {
            caller.removeSummonedMob(this.getObjectId());
        }

        this.calledMobOids = null;
    }

    /**
     * 执行 push、移除、After、动作 操作。
     * @param run run
     */
    public void pushRemoveAfterAction(Runnable run) {
        this.removeAfterAction = run;
    }

    /**
     * 执行 pop、移除、After、动作 操作。
     * @return Runnable 类型结果
     */
    public Runnable popRemoveAfterAction() {
        Runnable r = this.removeAfterAction;
        this.removeAfterAction = null;

        return r;
    }

    /**
     * 获取HP。
     * @return int 类型结果
     */
    public int getHp() {
        return hp.get();
    }

    /**
     * 添加HP。
     * @param hp hp
     * @return synchronized void 类型结果
     */
    public synchronized void addHp(int hp) {
        if (this.hp.get() <= 0) {
            return;
        }
        this.hp.addAndGet(hp);
    }

    /**
     * 设置Starting、HP。
     * @param hp hp
     * @return synchronized void 类型结果
     */
    public synchronized void setStartingHp(int hp) {
        stats.setHp(hp);    // refactored mob stats after non-static HP pool suggestion thanks to twigs
        this.hp.set(hp);
    }

    /**
     * 获取MaxHP。
     * @return int 类型结果
     */
    public int getMaxHp() {
        return stats.getHp();
    }

    /**
     * 获取MP。
     * @return int 类型结果
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置MP。
     * @param mp mp
     */
    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    /**
     * 获取MaxMP。
     * @return int 类型结果
     */
    public int getMaxMp() {
        return stats.getMp();
    }

    /**
     * 获取经验。
     * @return int 类型结果
     */
    public int getExp() {
        return stats.getExp();
    }

    /**
     * 获取等级。
     * @return int 类型结果
     */
    public int getLevel() {
        return stats.getLevel();
    }

    /**
     * 获取CP。
     * @return int 类型结果
     */
    public int getCP() {
        return stats.getCP();
    }

    /**
     * 获取队伍。
     * @return int 类型结果
     */
    public int getTeam() {
        return team;
    }

    /**
     * 设置队伍。
     * @param team team
     */
    public void setTeam(int team) {
        this.team = team;
    }

    /**
     * 获取Venom、Multi。
     * @return int 类型结果
     */
    public int getVenomMulti() {
        return this.VenomMultiplier;
    }

    /**
     * 设置Venom、Multi。
     * @param multiplier multiplier
     */
    public void setVenomMulti(int multiplier) {
        this.VenomMultiplier = multiplier;
    }

    /**
     * 获取属性。
     * @return MonsterStats 类型结果
     */
    public MonsterStats getStats() {
        return stats;
    }

    /**
     * 设置属性。
     * @param stats stats
     */
    public void setStats(MonsterStats stats) {
        this.stats = stats;
    }

    /**
     * 判断是否为Boss。
     * @return boolean 类型结果
     */
    public boolean isBoss() {
        return stats.isBoss();
    }

    /**
     * 获取动画时间。
     * @param name name
     * @return int 类型结果
     */
    public int getAnimationTime(String name) {
        return stats.getAnimationTime(name);
    }

    private List<Integer> getRevives() {
        return stats.getRevives();
    }

    private byte getTagColor() {
        return stats.getTagColor();
    }

    private byte getTagBgColor() {
        return stats.getTagBgColor();
    }

    /**
     * 设置HP、Zero。
     */
    public void setHpZero() {     // force HP = 0
        applyAndGetHpDamage(Integer.MAX_VALUE, false);
    }

    private boolean applyAnimationIfRoaming(int attackPos, MobSkill skill) {   // roam: not casting attack or skill animations
        if (!animationLock.tryLock()) {
            return false;
        }

        try {
            long animationTime;

            if (skill == null) {
                animationTime = MonsterInformationProvider.getInstance().getMobAttackAnimationTime(this.getId(), attackPos);
            } else {
                animationTime = MonsterInformationProvider.getInstance().getMobSkillAnimationTime(skill);
            }

            if (animationTime > 0) {
                MobAnimationService service = (MobAnimationService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_ANIMATION);
                return service.registerMobOnAnimationEffect(map.getId(), this.hashCode(), animationTime);
            } else {
                return true;
            }
        } finally {
            animationLock.unlock();
        }
    }

    /**
     * 应用与获取HP伤害。
     * @param delta delta
     * @param stayAlive stayAlive
     * @return synchronized Integer 类型结果
     */
    public synchronized Integer applyAndGetHpDamage(int delta, boolean stayAlive) {
        int curHp = hp.get();
        if (curHp <= 0) {       // this monster is already dead
            return null;
        }

        if (delta >= 0) {
            if (stayAlive) {
                curHp--;
            }
            int trueDamage = Math.min(curHp, delta);

            hp.addAndGet(-trueDamage);
            return trueDamage;
        } else {
            int trueHeal = -delta;
            int hp2Heal = curHp + trueHeal;
            int maxHp = getMaxHp();

            if (hp2Heal > maxHp) {
                trueHeal -= (hp2Heal - maxHp);
            }

            hp.addAndGet(trueHeal);
            return trueHeal;
        }
    }

    /**
     * 销毁/释放地图对象。
     * @return synchronized void 类型结果
     */
    public synchronized void disposeMapObject() {     // mob is no longer associated with the map it was in
        hp.set(-1);
    }

    /**
     * 向地图广播怪物HPBar。
     * @param from from
     */
    public void broadcastMobHpBar(Character from) {
        if (hasBossHPBar()) {
            from.setPlayerAggro(this.hashCode());
            from.getMap().broadcastBossHpMessage(this, this.hashCode(), makeBossHPBarPacket(), getPosition());
        } else if (!isBoss()) {
            int remainingHP = (int) Math.max(1, hp.get() * 100f / getMaxHp());
            Packet packet = PacketCreator.showMonsterHP(getObjectId(), remainingHP);
            if (from.getParty() != null) {
                for (PartyCharacter mpc : from.getParty().getMembers()) {
                    Character member = from.getMap().getCharacterById(mpc.getId()); // god bless
                    if (member != null) {
                        member.sendPacket(packet);
                    }
                }
            } else {
                from.sendPacket(packet);
            }
        }
    }

    /**
     * 执行 damage 操作。
     * @param attacker attacker
     * @param damage 伤害值
     * @param stayAlive stayAlive
     * @return boolean 类型结果
     */
    public boolean damage(Character attacker, int damage, boolean stayAlive) {
        boolean lastHit = false;

        this.lockMonster();
        try {
            if (!this.isAlive()) {
                return false;
            }

            /* pyramid not implemented
            Pair<Integer, Integer> cool = this.getStats().getCool();
            if (cool != null) {
                Pyramid pq = (Pyramid) chr.getPartyQuest();
                if (pq != null) {
                    if (damage > 0) {
                        if (damage >= cool.getLeft()) {
                            if ((Math.random() * 100) < cool.getRight()) {
                                pq.cool();
                            } else {
                                pq.kill();
                            }
                        } else {
                            pq.kill();
                        }
                    } else {
                        pq.miss();
                    }
                    killed = true;
                }
            }
            */

            if (damage > 0) {
                this.applyDamage(attacker, damage, stayAlive, false);
                if (!this.isAlive()) {  // monster just died
                    lastHit = true;
                }
            }
        } finally {
            this.unlockMonster();
        }

        return lastHit;
    }

    /**
     * @param from      the player that dealt the damage
     * @param damage
     * @param stayAlive
     */
    private void applyDamage(Character from, int damage, boolean stayAlive, boolean fake) {
        Integer trueDamage = applyAndGetHpDamage(damage, stayAlive);
        if (trueDamage == null) {
            return;
        }

        if (GameConfig.getServerBoolean("use_debug") && from.isGM()) {
            from.dropMessage(5, I18nUtil.getMessage("Monster.applyDamage.message1") + this.getId() + ", OID " + this.getObjectId());
        }

        if (!fake) {
            dispatchMonsterDamaged(from, trueDamage);
        }

        // ========== 通知事件实例记录伤害 ==========
        EventInstanceManager eim = getMap().getEventInstance();
        if (eim != null && !fake) {
            eim.addDamage(from, trueDamage);
        }

        if (!takenDamage.containsKey(from.getId())) {
            takenDamage.put(from.getId(), new AtomicLong(trueDamage));
        } else {
            takenDamage.get(from.getId()).addAndGet(trueDamage);
        }

        broadcastMobHpBar(from);
    }

    /**
     * 应用Fake、伤害。
     * @param from from
     * @param damage 伤害值
     * @param stayAlive stayAlive
     */
    public void applyFakeDamage(Character from, int damage, boolean stayAlive) {
        applyDamage(from, damage, stayAlive, true);
    }

    /**
     * 执行 heal 操作。
     * @param hp hp
     * @param mp mp
     */
    public void heal(int hp, int mp) {
        Integer hpHealed = applyAndGetHpDamage(-hp, false);
        if (hpHealed == null) {
            return;
        }

        int mp2Heal = getMp() + mp;
        int maxMp = getMaxMp();
        if (mp2Heal >= maxMp) {
            mp2Heal = maxMp;
        }
        setMp(mp2Heal);

        if (hp > 0) {
            getMap().broadcastMessage(PacketCreator.healMonster(getObjectId(), hp, getHp(), getMaxHp()));
        }

        maxHpPlusHeal.addAndGet(hpHealed);
        dispatchMonsterHealed(hpHealed);
    }

    /**
     * 判断是否为Attacked、按。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isAttackedBy(Character chr) {
        return takenDamage.containsKey(chr.getId());
    }

    private static boolean isWhiteExpGain(Character chr, Map<Integer, Float> personalRatio, double sdevRatio) {
        Float pr = personalRatio.get(chr.getId());
        if (pr == null) {
            return false;
        }

        return pr >= sdevRatio;
    }

    private static double calcExperienceStandDevThreshold(List<Float> entryExpRatio, int totalEntries) {
        float avgExpReward = 0.0f;
        for (Float exp : entryExpRatio) {
            avgExpReward += exp;
        }

        // thanks Simon (HarborMS) for finding an issue with solo party player gaining yellow EXP when soloing mobs
        avgExpReward /= totalEntries;

        float varExpReward = 0.0f;
        for (Float exp : entryExpRatio) {
            varExpReward += Math.pow(exp - avgExpReward, 2);
        }
        varExpReward /= entryExpRatio.size();

        return avgExpReward + Math.sqrt(varExpReward);
    }

    private void distributePlayerExperience(Character chr, float exp, float partyBonusMod, int totalPartyLevel, boolean highestPartyDamager, boolean whiteExpGain, boolean hasPartySharers) {
        float playerExp = (GameConfig.getServerFloat("exp_split_common_mod") * chr.getLevel()) / totalPartyLevel;
        if (highestPartyDamager) {
            playerExp += GameConfig.getServerFloat("exp_split_mvp_mod");
        }

        playerExp *= exp;
        float bonusExp = partyBonusMod * playerExp;

        this.giveExpToCharacter(chr, playerExp, bonusExp, whiteExpGain, hasPartySharers);
        giveFamilyRep(chr.getFamilyEntry());
    }

    private void distributePartyExperience(Map<Character, Long> partyParticipation, float expPerDmg, Set<Character> underleveled, Map<Integer, Float> personalRatio, double sdevRatio) {
        IntervalBuilder leechInterval = new IntervalBuilder();
        leechInterval.addInterval(this.getLevel() - GameConfig.getServerInt("exp_split_level_interval"), this.getLevel() + GameConfig.getServerInt("exp_split_level_interval"));

        long maxDamage = 0, partyDamage = 0;
        Character participationMvp = null;
        for (Entry<Character, Long> e : partyParticipation.entrySet()) {
            long entryDamage = e.getValue();
            partyDamage += entryDamage;

            if (maxDamage < entryDamage) {
                maxDamage = entryDamage;
                participationMvp = e.getKey();
            }

            // thanks Thora for pointing out leech level limitation
            int chrLevel = e.getKey().getLevel();
            leechInterval.addInterval(chrLevel - GameConfig.getServerInt("exp_split_leech_interval"), chrLevel + GameConfig.getServerInt("exp_split_leech_interval"));
        }

        List<Character> expMembers = new LinkedList<>();
        int totalPartyLevel = 0;

        // thanks G h o s t, Alfred, Vcoc, BHB for poiting out a bug in detecting party members after membership transactions in a party took place
        if (GameConfig.getServerBoolean("use_enforce_mob_level_range")) {
            for (Character member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
                if (!leechInterval.inInterval(member.getLevel())) {
                    underleveled.add(member);
                    continue;
                }

                totalPartyLevel += member.getLevel();
                expMembers.add(member);
            }
        } else {    // thanks Ari for noticing unused server flag after EXP system overhaul
            for (Character member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
                totalPartyLevel += member.getLevel();
                expMembers.add(member);
            }
        }

        int membersSize = expMembers.size();
        float participationExp = partyDamage * expPerDmg;

        // thanks Crypter for reporting an insufficiency on party exp bonuses
        boolean hasPartySharers = membersSize > 1;
        float partyBonusMod = hasPartySharers ? 0.05f * membersSize : 0.0f;

        for (Character mc : expMembers) {
            distributePlayerExperience(mc, participationExp, partyBonusMod, totalPartyLevel, mc == participationMvp, isWhiteExpGain(mc, personalRatio, sdevRatio), hasPartySharers);
            giveFamilyRep(mc.getFamilyEntry());
        }
    }

    private void distributeExperience(int killerId) {
        if (isAlive()) {
            return;
        }

        Map<Party, Map<Character, Long>> partyExpDist = new HashMap<>();
        Map<Character, Long> soloExpDist = new HashMap<>();

        Map<Integer, Character> mapPlayers = map.getMapAllPlayers();

        int totalEntries = 0;   // counts "participant parties", players who no longer are available in the map is an "independent party"
        for (Entry<Integer, AtomicLong> e : takenDamage.entrySet()) {
            Character chr = mapPlayers.get(e.getKey());
            if (chr != null) {
                long damage = e.getValue().longValue();

                Party p = chr.getParty();
                if (p != null) {
                    Map<Character, Long> partyParticipation = partyExpDist.get(p);
                    if (partyParticipation == null) {
                        partyParticipation = new HashMap<>(6);
                        partyExpDist.put(p, partyParticipation);

                        totalEntries += 1;
                    }

                    partyParticipation.put(chr, damage);
                } else {
                    soloExpDist.put(chr, damage);
                    totalEntries += 1;
                }
            } else {
                totalEntries += 1;
            }
        }

        long totalDamage = maxHpPlusHeal.get();
        int mobExp = getExp();
        float expPerDmg = ((float) mobExp) / totalDamage;

        Map<Integer, Float> personalRatio = new HashMap<>();
        List<Float> entryExpRatio = new LinkedList<>();
        for (Entry<Character, Long> e : soloExpDist.entrySet()) {
            float ratio = ((float) e.getValue()) / totalDamage;

            personalRatio.put(e.getKey().getId(), ratio);
            entryExpRatio.add(ratio);
        }

        for (Map<Character, Long> m : partyExpDist.values()) {
            float ratio = 0.0f;
            for (Entry<Character, Long> e : m.entrySet()) {
                float chrRatio = ((float) e.getValue()) / totalDamage;

                personalRatio.put(e.getKey().getId(), chrRatio);
                ratio += chrRatio;
            }

            entryExpRatio.add(ratio);
        }

        double sdevRatio = calcExperienceStandDevThreshold(entryExpRatio, totalEntries);

        // GMS-like player and party split calculations found thanks to Russt, KaidaTan, Dusk, AyumiLove - src: https://ayumilovemaple.wordpress.com/maplestory_calculator_formula/
        Set<Character> underleveled = new HashSet<>();
        for (Entry<Character, Long> chrParticipation : soloExpDist.entrySet()) {
            float exp = chrParticipation.getValue() * expPerDmg;
            Character chr = chrParticipation.getKey();

            distributePlayerExperience(chr, exp, 0.0f, chr.getLevel(), true, isWhiteExpGain(chr, personalRatio, sdevRatio), false);
        }

        for (Map<Character, Long> partyParticipation : partyExpDist.values()) {
            distributePartyExperience(partyParticipation, expPerDmg, underleveled, personalRatio, sdevRatio);
        }

        EventInstanceManager eim = getMap().getEventInstance();
        if (eim != null) {
            Character chr = mapPlayers.get(killerId);
            if (chr != null) {
                eim.monsterKilled(chr, this);
            }
        }

        for (Character mc : underleveled) {
            mc.showUnderLeveledInfo(this);
        }

    }

    private float getStatusExpMultiplier(Character attacker, boolean hasPartySharers) {
        float multiplier = 1.0f;

        // thanks Prophecy & Aika for finding out Holy Symbol not being applied on party bonuses
        Integer holySymbol = attacker.getBuffedValue(BuffStat.HOLY_SYMBOL);
        if (holySymbol != null) {
            if (GameConfig.getServerBoolean("use_full_holy_symbol")) { // thanks Mordred, xinyifly, AyumiLove, andy33 for noticing HS hands out 20% of its potential on less than 3 players
                multiplier *= (1.0 + (holySymbol.doubleValue() / 100.0));
            } else {
                multiplier *= (1.0 + (holySymbol.doubleValue() / (hasPartySharers ? 100.0 : 500.0)));
            }
        }

        statiLock.lock();
        try {
            MonsterStatusEffect mse = stati.get(MonsterStatus.SHOWDOWN);
            if (mse != null) {
                multiplier *= (1.0 + (mse.getStati().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0));
            }
        } finally {
            statiLock.unlock();
        }

        return multiplier;
    }

    private static int expValueToInteger(double exp) {
        if (exp > Integer.MAX_VALUE) {
            exp = Integer.MAX_VALUE;
        } else if (exp < Integer.MIN_VALUE) {
            exp = Integer.MIN_VALUE;
        }

        return (int) Math.round(exp);    // operations on float point are not point-precise... thanks IxianMace for noticing -1 EXP gains
    }

    private void giveExpToCharacter(Character attacker, Float personalExp, Float partyExp, boolean white, boolean hasPartySharers) {
        if (attacker.isAlive()) {
            if (personalExp != null) {
                personalExp *= getStatusExpMultiplier(attacker, hasPartySharers);
                personalExp *= (attacker.getExpRate() * attacker.getMobExpRate());
            } else {
                personalExp = 0.0f;
            }

            Integer expBonus = attacker.getBuffedValue(BuffStat.EXP_INCREASE);
            if (expBonus != null) {     // exp increase player buff found thanks to HighKey21
                personalExp += expBonus;
            }

            Integer expBuff = attacker.getBuffedValue(BuffStat.EXP_BUFF);
            if (expBuff != null) {
                personalExp *= 2;
            }

            if(attacker.isFamilyBuff()){
                personalExp *= attacker.getFamilyExp();
            }

            int _personalExp = expValueToInteger(personalExp); // assuming no negative xp here

            if (partyExp != null) {
                partyExp *= getStatusExpMultiplier(attacker, hasPartySharers);
                partyExp *= (attacker.getExpRate() * attacker.getMobExpRate());
                partyExp *= GameConfig.getServerFloat("party_bonus_exp_rate");
            } else {
                partyExp = 0.0f;
            }

            int _partyExp = expValueToInteger(partyExp);

            attacker.gainExp(_personalExp, _partyExp, true, false, white);
            attacker.increaseEquipExp(_personalExp);
            attacker.raiseQuestMobCount(getId());
            VeteranHunterMedal.onMonsterKilled(attacker, this);
        }
    }

    /**
     * 检索相关掉落。
     * @return List<MonsterDropEntry> 类型结果
     */
    public List<MonsterDropEntry> retrieveRelevantDrops() {
        if (this.getStats().isFriendly()) {     // thanks Conrad for noticing friendly mobs not spawning loots after a recent update
            return MonsterInformationProvider.getInstance().retrieveEffectiveDrop(this.getId());
        }

        Map<Integer, Character> pchars = map.getMapAllPlayers();

        List<Character> lootChars = new LinkedList<>();
        for (Integer cid : takenDamage.keySet()) {
            Character chr = pchars.get(cid);
            if (chr != null && chr.isLoggedInWorld()) {
                lootChars.add(chr);
            }
        }

        return LootManager.retrieveRelevantDrops(this.getId(), lootChars);
    }

    /**
     * 击杀按。
     * @param killer killer
     * @return Character 类型结果
     */
    public Character killBy(final Character killer) {
        distributeExperience(killer != null ? killer.getId() : 0);

        final Pair<Character, Boolean> lastController = aggroRemoveController();
        final List<Integer> toSpawn = this.getRevives();
        if (toSpawn != null) {
            final MapleMap reviveMap = map;
            if (toSpawn.contains(MobId.TRANSPARENT_ITEM) && reviveMap.getId() > 925000000 && reviveMap.getId() < 926000000) {
                reviveMap.broadcastMessage(PacketCreator.playSound("Dojang/clear"));
                reviveMap.broadcastMessage(PacketCreator.showEffect("dojang/end/clear"));
            }
            Pair<Integer, String> timeMob = reviveMap.getTimeMob();
            if (timeMob != null) {
                if (toSpawn.contains(timeMob.getLeft())) {
                    reviveMap.broadcastMessage(PacketCreator.serverNotice(6, timeMob.getRight()));
                }
            }

            if (toSpawn.size() > 0) {
                final EventInstanceManager eim = this.getMap().getEventInstance();

                TimerManager.getInstance().schedule(() -> {
                    Character controller = lastController.getLeft();
                    boolean aggro = lastController.getRight();

                    for (Integer mid : toSpawn) {
                        final Monster mob = LifeFactory.getMonster(mid);
                        mob.setPosition(getPosition());
                        mob.setFh(getFh());
                        mob.setParentMobOid(getObjectId());

                        if (dropsDisabled()) {
                            mob.disableDrops();
                        }
                        reviveMap.spawnMonster(mob);

                        if (MobId.isDeadHorntailPart(mob.getId()) && reviveMap.isHorntailDefeated()) {
                            boolean htKilled = false;
                            Monster ht = reviveMap.getMonsterById(MobId.HORNTAIL);

                            if (ht != null) {
                                ht.lockMonster();
                                try {
                                    htKilled = ht.isAlive();
                                    ht.setHpZero();
                                } finally {
                                    ht.unlockMonster();
                                }

                                if (htKilled) {
                                    reviveMap.killMonster(ht, killer, true);
                                }
                            }

                            for (int i = MobId.DEAD_HORNTAIL_MAX; i >= MobId.DEAD_HORNTAIL_MIN; i--) {
                                reviveMap.killMonster(reviveMap.getMonsterById(i), killer, true);
                            }
                        } else if (controller != null) {
                            mob.aggroSwitchController(controller, aggro);
                        }

                        if (eim != null) {
                            eim.reviveMonster(mob);
                        }
                    }
                }, getAnimationTime("die1"));
            }
        } else {  // is this even necessary?
            log.warn("[CRITICAL LOSS] toSpawn is null for {}", getName());
        }

        Character looter = map.getCharacterById(getHighestDamagerId());
        return looter != null ? looter : killer;
    }

    /**
     * 掉落来自友好怪物。
     * @param delay 延迟（毫秒）
     */
    public void dropFromFriendlyMonster(long delay) {
        final Monster m = this;
        monsterItemDrop = TimerManager.getInstance().register(() -> {
            if (!m.isAlive()) {
                if (monsterItemDrop != null) {
                    monsterItemDrop.cancel(false);
                }

                return;
            }

            MapleMap map = m.getMap();
            List<Character> chrList = map.getAllPlayers();
            if (!chrList.isEmpty()) {
                Character chr = chrList.get(0);

                EventInstanceManager eim = map.getEventInstance();
                if (eim != null) {
                    eim.friendlyItemDrop(m);
                }

                map.dropFromFriendlyMonster(chr, m);
            }
        }, delay, delay);
    }

    private void dispatchRaiseQuestMobCount() {
        Set<Integer> attackerChrids = takenDamage.keySet();
        if (!attackerChrids.isEmpty()) {
            Map<Integer, Character> mapChars = map.getMapPlayers();
            if (!mapChars.isEmpty()) {
                int mobid = getId();

                for (Integer chrid : attackerChrids) {
                    Character chr = mapChars.get(chrid);

                    if (chr != null && chr.isLoggedInWorld()) {
                        chr.raiseQuestMobCount(mobid);
                    }
                }
            }
        }
    }

    /**
     * 执行 dispatch、怪物、Killed 操作。
     * @param hasKiller hasKiller
     */
    public void dispatchMonsterKilled(boolean hasKiller) {
        processMonsterKilled(hasKiller);

        EventInstanceManager eim = getMap().getEventInstance();
        if (eim != null) {
            if (!this.getStats().isFriendly()) {
                eim.monsterKilled(this, hasKiller);
            } else {
                eim.friendlyKilled(this, hasKiller);
            }
        }
    }

    private synchronized void processMonsterKilled(boolean hasKiller) {
        if (!hasKiller) {    // players won't gain EXP from a mob that has no killer, but a quest count they should
            dispatchRaiseQuestMobCount();
        }

        this.aggroClearDamages();
        this.dispatchClearSummons();

        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterKilled(getAnimationTime("die1"));
        }

        statiLock.lock();
        try {
            stati.clear();
            alreadyBuffed.clear();
            listeners.clear();
        } finally {
            statiLock.unlock();
        }
    }

    private void dispatchMonsterDamaged(Character from, int trueDmg) {
        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterDamaged(from, trueDmg);
        }
    }

    private void dispatchMonsterHealed(int trueHeal) {
        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterHealed(trueHeal);
        }
    }

    private void giveFamilyRep(FamilyEntry entry) {
        if (entry != null) {
            int repGain = isBoss() ? GameConfig.getServerInt("family_rep_per_boss_kill") : GameConfig.getServerInt("family_rep_per_kill");
            if (getMaxHp() <= 1) {
                repGain = 0; //don't count trash mobs
            }
            entry.giveReputationToSenior(repGain, true);
        }
    }

    /**
     * 获取Highest、Damager、ID。
     * @return int 类型结果
     */
    public int getHighestDamagerId() {
        int curId = 0;
        long curDmg = 0;

        for (Entry<Integer, AtomicLong> damage : takenDamage.entrySet()) {
            curId = damage.getValue().get() >= curDmg ? damage.getKey() : curId;
            curDmg = damage.getKey() == curId ? damage.getValue().get() : curDmg;
        }

        return curId;
    }

    /**
     * 判断是否为存活。
     * @return boolean 类型结果
     */
    public boolean isAlive() {
        return this.hp.get() > 0;
    }

    /**
     * 添加监听器。
     * @param listener listener
     */
    public void addListener(MonsterListener listener) {
        statiLock.lock();
        try {
            listeners.add(listener);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 获取控制器。
     * @return Character 类型结果
     */
    public Character getController() {
        return controller.get();
    }

    private void setController(Character controller) {
        this.controller = new WeakReference<>(controller);
    }

    /**
     * 判断是否为控制器Has仇恨。
     * @return boolean 类型结果
     */
    public boolean isControllerHasAggro() {
        return !fake && controllerHasAggro;
    }

    private void setControllerHasAggro(boolean controllerHasAggro) {
        if (!fake) {
            this.controllerHasAggro = controllerHasAggro;
        }
    }

    /**
     * 判断是否为控制器、Knows、About、仇恨。
     * @return boolean 类型结果
     */
    public boolean isControllerKnowsAboutAggro() {
        return !fake && controllerKnowsAboutAggro;
    }

    private void setControllerKnowsAboutAggro(boolean controllerKnowsAboutAggro) {
        if (!fake) {
            this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
        }
    }

    private void setControllerHasPuppet(boolean controllerHasPuppet) {
        this.controllerHasPuppet = controllerHasPuppet;
    }

    /**
     * 执行 make、Boss、H、P、Bar、数据包 操作。
     * @return Packet 类型结果
     */
    public Packet makeBossHPBarPacket() {
        return PacketCreator.showBossHP(getId(), getHp(), getMaxHp(), getTagColor(), getTagBgColor());
    }

    /**
     * 判断是否拥有Boss、H、P、Bar。
     * @return boolean 类型结果
     */
    public boolean hasBossHPBar() {
        return isBoss() && getTagColor() > 0;
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        if (hp.get() <= 0) { // mustn't monsterLock this function
            return;
        }
        if (fake) {
            client.sendPacket(PacketCreator.spawnFakeMonster(this, 0));
        } else {
            client.sendPacket(PacketCreator.spawnMonster(this, false));
        }

        if (hasBossHPBar()) {
            client.announceBossHpBar(this, this.hashCode(), makeBossHPBarPacket());
        }
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.killMonster(getObjectId(), false));
        client.sendPacket(PacketCreator.killMonster(getObjectId(), true));
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.MONSTER;
    }

    /**
     * 判断是否为Mobile。
     * @return boolean 类型结果
     */
    public boolean isMobile() {
        return stats.isMobile();
    }

    /**
     * 判断是否为Facing、剩余。
     * @return boolean 类型结果
     */
    @Override
    public boolean isFacingLeft() {
        int fixedStance = stats.getFixedStance();    // thanks DimDiDima for noticing inconsistency on some AOE mobskills
        if (fixedStance != 0) {
            return Math.abs(fixedStance) % 2 == 1;
        }

        return super.isFacingLeft();
    }

    /**
     * 获取元素克制。
     * @param e e
     * @return ElementalEffectiveness 类型结果
     */
    public ElementalEffectiveness getElementalEffectiveness(Element e) {
        statiLock.lock();
        try {
            if (stati.get(MonsterStatus.DOOM) != null) {
                return ElementalEffectiveness.NORMAL; // like blue snails
            }
        } finally {
            statiLock.unlock();
        }

        return getMonsterEffectiveness(e);
    }

    private ElementalEffectiveness getMonsterEffectiveness(Element e) {
        monsterLock.lock();
        try {
            return stats.getEffectiveness(e);
        } finally {
            monsterLock.unlock();
        }
    }

    private Character getActiveController() {
        Character chr = getController();

        if (chr != null && chr.isLoggedInWorld() && chr.getMap() == this.getMap()) {
            return chr;
        } else {
            return null;
        }
    }

    private void broadcastMonsterStatusMessage(Packet packet) {
        map.broadcastMessage(packet, getPosition());

        Character chrController = getActiveController();
        if (chrController != null && !chrController.isMapObjectVisible(Monster.this)) {
            chrController.sendPacket(packet);
        }
    }

    private int broadcastStatusEffect(final MonsterStatusEffect status) {
        int animationTime = status.getSkill().getAnimationTime();
        Packet packet = PacketCreator.applyMonsterStatus(getObjectId(), status, null);
        broadcastMonsterStatusMessage(packet);

        return animationTime;
    }

    /**
     * 应用状态。
     * @param from from
     * @param status status
     * @param poison poison
     * @param duration duration
     * @return boolean 类型结果
     */
    public boolean applyStatus(Character from, final MonsterStatusEffect status, boolean poison, long duration) {
        return applyStatus(from, status, poison, duration, false);
    }

    /**
     * 应用状态。
     * @param from from
     * @param status status
     * @param poison poison
     * @param duration duration
     * @param venom venom
     * @return boolean 类型结果
     */
    public boolean applyStatus(Character from, final MonsterStatusEffect status, boolean poison, long duration, boolean venom) {
        switch (getMonsterEffectiveness(status.getSkill().getElement())) {
            case IMMUNE:
            case STRONG:
            case NEUTRAL:
                return false;
            case NORMAL:
            case WEAK:
                break;
            default: {
                log.warn("Unknown elemental effectiveness: {}", getMonsterEffectiveness(status.getSkill().getElement()));
                return false;
            }
        }

        if (status.getSkill().getId() == FPMage.ELEMENT_COMPOSITION) { // fp compo
            ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.POISON);
            if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                return false;
            }
        } else if (status.getSkill().getId() == ILMage.ELEMENT_COMPOSITION) { // il compo
            ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.ICE);
            if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                return false;
            }
        } else if (status.getSkill().getId() == NightLord.VENOMOUS_STAR || status.getSkill().getId() == Shadower.VENOMOUS_STAB || status.getSkill().getId() == NightWalker.VENOM) {// venom
            if (getMonsterEffectiveness(Element.POISON) == ElementalEffectiveness.WEAK) {
                return false;
            }
        }
        if (poison && hp.get() <= 1) {
            return false;
        }

        final Map<MonsterStatus, Integer> statis = status.getStati();
        if (stats.isBoss()) {
            if (!(statis.containsKey(MonsterStatus.SPEED)
                    && statis.containsKey(MonsterStatus.NINJA_AMBUSH)
                    && statis.containsKey(MonsterStatus.WATK))) {
                return false;
            }
        }

        final Channel ch = map.getChannelServer();
        final int mapid = map.getId();
        if (statis.size() > 0) {
            statiLock.lock();
            try {
                for (MonsterStatus stat : statis.keySet()) {
                    final MonsterStatusEffect oldEffect = stati.get(stat);
                    if (oldEffect != null) {
                        oldEffect.removeActiveStatus(stat);
                        if (oldEffect.getStati().isEmpty()) {
                            MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                            service.interruptMobStatus(mapid, oldEffect);
                        }
                    }
                }
            } finally {
                statiLock.unlock();
            }
        }

        final Runnable cancelTask = () -> {
            if (isAlive()) {
                Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), status.getStati());
                broadcastMonsterStatusMessage(packet);
            }

            statiLock.lock();
            try {
                for (MonsterStatus stat : status.getStati().keySet()) {
                    stati.remove(stat);
                }
            } finally {
                statiLock.unlock();
            }

            setVenomMulti(0);
        };

        Runnable overtimeAction = null;
        int overtimeDelay = -1;

        int animationTime;
        if (poison) {
            int poisonLevel = from.getSkillLevel(status.getSkill());
            int poisonDamage = Math.min(Short.MAX_VALUE, (int) (getMaxHp() / (70.0 - poisonLevel) + 0.999));
            status.setValue(MonsterStatus.POISON, poisonDamage);
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(poisonDamage, from, status, 0);
            overtimeDelay = 1000;
        } else if (venom) {
            if (from.getJob() == Job.NIGHTLORD || from.getJob() == Job.SHADOWER || from.getJob().isA(Job.NIGHTWALKER3)) {
                int poisonLevel, matk, jobid = from.getJob().getId();
                int skillid = (jobid == 412 ? NightLord.VENOMOUS_STAR : (jobid == 422 ? Shadower.VENOMOUS_STAB : NightWalker.VENOM));
                poisonLevel = from.getSkillLevel(SkillFactory.getSkill(skillid));
                if (poisonLevel <= 0) {
                    return false;
                }
                matk = SkillFactory.getSkill(skillid).getEffect(poisonLevel).getMatk();
                int luk = from.getLuk();
                int maxDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.2 * luk * matk));
                int minDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.1 * luk * matk));
                int gap = maxDmg - minDmg;
                if (gap == 0) {
                    gap = 1;
                }
                int poisonDamage = 0;
                for (int i = 0; i < getVenomMulti(); i++) {
                    poisonDamage += (Randomizer.nextInt(gap) + minDmg);
                }
                poisonDamage = Math.min(Short.MAX_VALUE, poisonDamage);
                status.setValue(MonsterStatus.VENOMOUS_WEAPON, poisonDamage);
                status.setValue(MonsterStatus.POISON, poisonDamage);
                animationTime = broadcastStatusEffect(status);

                overtimeAction = new DamageTask(poisonDamage, from, status, 0);
                overtimeDelay = 1000;
            } else {
                return false;
            }
            /*
        } else if (status.getSkill().getId() == Hermit.SHADOW_WEB || status.getSkill().getId() == NightWalker.SHADOW_WEB) { //Shadow Web
            int webDamage = (int) (getMaxHp() / 50.0 + 0.999);
            status.setValue(MonsterStatus.SHADOW_WEB, Integer.valueOf(webDamage));
            animationTime = broadcastStatusEffect(status);
            
            overtimeAction = new DamageTask(webDamage, from, status, 1);
            overtimeDelay = 3500;
            */
        } else if (status.getSkill().getId() == 4121004 || status.getSkill().getId() == 4221004) { // Ninja Ambush
            final Skill skill = SkillFactory.getSkill(status.getSkill().getId());
            final byte level = from.getSkillLevel(skill);
            final int damage = (int) ((from.getStr() + from.getLuk()) * ((3.7 * skill.getEffect(level).getDamage()) / 100));

            status.setValue(MonsterStatus.NINJA_AMBUSH, damage);
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(damage, from, status, 2);
            overtimeDelay = 1000;
        } else {
            animationTime = broadcastStatusEffect(status);
        }

        statiLock.lock();
        try {
            for (MonsterStatus stat : status.getStati().keySet()) {
                stati.put(stat, status);
                alreadyBuffed.add(stat);
            }
        } finally {
            statiLock.unlock();
        }

        MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
        service.registerMobStatus(mapid, status, cancelTask, duration + animationTime - 100, overtimeAction, overtimeDelay);
        return true;
    }

    /**
     * 执行 dispel、技能 操作。
     * @param skill skill
     */
    public final void dispelSkill(final MobSkill skill) {
        List<MonsterStatus> toCancel = new ArrayList<>();
        for (Entry<MonsterStatus, MonsterStatusEffect> effects : stati.entrySet()) {
            MonsterStatusEffect mse = effects.getValue();
            if (mse.getMobSkill() != null && mse.getMobSkill().getType() == skill.getType()) { //not checking for level.
                toCancel.add(effects.getKey());
            }
        }
        for (MonsterStatus stat : toCancel) {
            debuffMobStat(stat);
        }
    }

    /**
     * 应用怪物、Buff。
     * @param stats stats（MonsterStatus, Integer 列表/集合）
     * @param x x
     * @param duration duration
     * @param skill skill
     * @param reflection reflection（Integer 列表/集合）
     */
    public void applyMonsterBuff(final Map<MonsterStatus, Integer> stats, final int x, long duration, MobSkill skill, final List<Integer> reflection) {
        final Runnable cancelTask = () -> {
            if (isAlive()) {
                Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), stats);
                broadcastMonsterStatusMessage(packet);

                statiLock.lock();
                try {
                    for (final MonsterStatus stat : stats.keySet()) {
                        stati.remove(stat);
                    }
                } finally {
                    statiLock.unlock();
                }
            }
        };
        final MonsterStatusEffect effect = new MonsterStatusEffect(stats, null, skill, true);
        Packet packet = PacketCreator.applyMonsterStatus(getObjectId(), effect, reflection);
        broadcastMonsterStatusMessage(packet);

        statiLock.lock();
        try {
            for (MonsterStatus stat : stats.keySet()) {
                stati.put(stat, effect);
                alreadyBuffed.add(stat);
            }
        } finally {
            statiLock.unlock();
        }

        MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
        service.registerMobStatus(map.getId(), effect, cancelTask, duration);
    }

    /**
     * 执行 refresh、怪物、位置 操作。
     */
    public void refreshMobPosition() {
        resetMobPosition(getPosition());
    }

    /**
     * 重置怪物位置。
     * @param newPoint newPoint
     */
    public void resetMobPosition(Point newPoint) {
        aggroRemoveController();

        setPosition(newPoint);
        map.broadcastMessage(PacketCreator.moveMonster(this.getObjectId(), false, -1, 0, 0, 0, this.getPosition(), this.getIdleMovement(), AbstractAnimatedMapObject.IDLE_MOVEMENT_PACKET_LENGTH));
        map.moveMonster(this, this.getPosition());

        aggroUpdateController();
    }

    private void debuffMobStat(MonsterStatus stat) {
        MonsterStatusEffect oldEffect;
        statiLock.lock();
        try {
            oldEffect = stati.remove(stat);
        } finally {
            statiLock.unlock();
        }

        if (oldEffect != null) {
            Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), oldEffect.getStati());
            broadcastMonsterStatusMessage(packet);
        }
    }

    /**
     * 执行 debuff、怪物 操作。
     * @param skillid skillid
     */
    public void debuffMob(int skillid) {
        MonsterStatus[] statups = {MonsterStatus.WEAPON_ATTACK_UP, MonsterStatus.WEAPON_DEFENSE_UP, MonsterStatus.MAGIC_ATTACK_UP, MonsterStatus.MAGIC_DEFENSE_UP};
        statiLock.lock();
        try {
            if (skillid == Hermit.SHADOW_MESO) {
                debuffMobStat(statups[1]);
                debuffMobStat(statups[3]);
            } else if (skillid == Priest.DISPEL) {
                for (MonsterStatus ms : statups) {
                    debuffMobStat(ms);
                }
            } else {    // is a crash skill
                int i = (skillid == Crusader.ARMOR_CRASH ? 1 : (skillid == WhiteKnight.MAGIC_CRASH ? 2 : 0));
                debuffMobStat(statups[i]);

                if (GameConfig.getServerBoolean("use_anti_immunity_crash")) {
                    if (skillid == Crusader.ARMOR_CRASH) {
                        if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                            debuffMobStat(MonsterStatus.WEAPON_IMMUNITY);
                        }
                        if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                            debuffMobStat(MonsterStatus.MAGIC_IMMUNITY);
                        }
                    } else if (skillid == WhiteKnight.MAGIC_CRASH) {
                        if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                            debuffMobStat(MonsterStatus.MAGIC_IMMUNITY);
                        }
                    } else {
                        if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                            debuffMobStat(MonsterStatus.WEAPON_IMMUNITY);
                        }
                    }
                }
            }
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 判断是否为Buffed。
     * @param status status
     * @return boolean 类型结果
     */
    public boolean isBuffed(MonsterStatus status) {
        statiLock.lock();
        try {
            return stati.containsKey(status);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 设置Fake。
     * @param fake fake
     */
    public void setFake(boolean fake) {
        monsterLock.lock();
        try {
            this.fake = fake;
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 判断是否为Fake。
     * @return boolean 类型结果
     */
    public boolean isFake() {
        monsterLock.lock();
        try {
            return fake;
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 获取地图。
     * @return MapleMap 类型结果
     */
    public MapleMap getMap() {
        return map;
    }

    /**
     * 获取地图、仇恨、Coordinator。
     * @return MonsterAggroCoordinator 类型结果
     */
    public MonsterAggroCoordinator getMapAggroCoordinator() {
        return map.getAggroCoordinator();
    }

    /**
     * 获取Skills。
     * @return Set<MobSkillId> 类型结果
     */
    public Set<MobSkillId> getSkills() {
        return stats.getSkills();
    }

    /**
     * 判断是否拥有技能。
     * @param skillId skillId
     * @param level level
     * @return boolean 类型结果
     */
    public boolean hasSkill(int skillId, int level) {
        return stats.hasSkill(skillId, level);
    }

    /**
     * 判断是否可以Use技能。
     * @param toUse toUse
     * @param apply apply
     * @return boolean 类型结果
     */
    public boolean canUseSkill(MobSkill toUse, boolean apply) {
        if (toUse == null || isBuffed(MonsterStatus.SEAL_SKILL)) {
            return false;
        }

        if (isReflectSkill(toUse)) {
            if (this.isBuffed(MonsterStatus.WEAPON_REFLECT) || this.isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                return false;
            }
        }

        monsterLock.lock();
        try {
            if (usedSkills.contains(toUse.getId())) {
                return false;
            }

            int mpCon = toUse.getMpCon();
            if (mp < mpCon) {
                return false;
            }
            
            /*
            if (!this.applyAnimationIfRoaming(-1, toUse)) {
                return false;
            }
            */

            if (apply) {
                this.usedSkill(toUse);
            }
        } finally {
            monsterLock.unlock();
        }

        return true;
    }

    private boolean isReflectSkill(MobSkill mobSkill) {
        return switch (mobSkill.getType()) {
            case PHYSICAL_COUNTER, MAGIC_COUNTER, PHYSICAL_AND_MAGIC_COUNTER -> true;
            default -> false;
        };
    }

    private void usedSkill(MobSkill skill) {
        final MobSkillId msId = skill.getId();
        monsterLock.lock();
        try {
            mp -= skill.getMpCon();

            this.usedSkills.add(msId);
        } finally {
            monsterLock.unlock();
        }

        final Monster mons = this;
        MapleMap mmap = mons.getMap();
        Runnable r = () -> mons.clearSkill(skill.getId());

        MobClearSkillService service = (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
        service.registerMobClearSkillAction(mmap.getId(), r, skill.getCoolTime());
    }

    private void clearSkill(MobSkillId msId) {
        monsterLock.lock();
        try {
            usedSkills.remove(msId);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 判断是否可以Use攻击。
     * @param attackPos attackPos
     * @param isSkill isSkill
     * @return int 类型结果
     */
    public int canUseAttack(int attackPos, boolean isSkill) {
        monsterLock.lock();
        try {
            /*
            if (usedAttacks.contains(attackPos)) {
                return -1;
            }
            */

            Pair<Integer, Integer> attackInfo = MonsterInformationProvider.getInstance().getMobAttackInfo(this.getId(), attackPos);
            if (attackInfo == null) {
                return -1;
            }

            int mpCon = attackInfo.getLeft();
            if (mp < mpCon) {
                return -1;
            }
            
            /*
            if (!this.applyAnimationIfRoaming(attackPos, null)) {
                return -1;
            }
            */

            usedAttack(attackPos, mpCon, attackInfo.getRight());
            return 1;
        } finally {
            monsterLock.unlock();
        }
    }

    private void usedAttack(final int attackPos, int mpCon, int cooltime) {
        monsterLock.lock();
        try {
            mp -= mpCon;
            usedAttacks.add(attackPos);

            final Monster mons = this;
            MapleMap mmap = mons.getMap();
            Runnable r = () -> mons.clearAttack(attackPos);

            MobClearSkillService service = (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
            service.registerMobClearSkillAction(mmap.getId(), r, cooltime);
        } finally {
            monsterLock.unlock();
        }
    }

    private void clearAttack(int attackPos) {
        monsterLock.lock();
        try {
            usedAttacks.remove(attackPos);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 判断是否拥有Any技能。
     * @return boolean 类型结果
     */
    public boolean hasAnySkill() {
        return this.stats.getNoSkills() > 0;
    }

    /**
     * 获取Random、技能。
     * @return MobSkillId 类型结果
     */
    public MobSkillId getRandomSkill() {
        Set<MobSkillId> skills = stats.getSkills();
        if (skills.size() == 0) {
            return null;
        }
        // There is no simple way of getting a random element from a Set. Have to make do with this.
        return skills.stream()
                .skip(Randomizer.nextInt(skills.size()))
                .findAny()
                .orElse(null);
    }

    /**
     * 判断是否为First、攻击。
     * @return boolean 类型结果
     */
    public boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    /**
     * 获取Buff、到、Give。
     * @return int 类型结果
     */
    public int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    private final class DamageTask implements Runnable {

        private final int dealDamage;
        private final Character chr;
        private final MonsterStatusEffect status;
        private final int type;
        private final MapleMap map;

        private DamageTask(int dealDamage, Character chr, MonsterStatusEffect status, int type) {
            this.dealDamage = dealDamage;
            this.chr = chr;
            this.status = status;
            this.type = type;
            this.map = chr.getMap();
        }

        /**
         * 执行动作逻辑。
         */
        @Override
        public void run() {
            int curHp = hp.get();
            if (curHp <= 1) {
                MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                service.interruptMobStatus(map.getId(), status);
                return;
            }

            int damage = dealDamage;
            if (damage >= curHp) {
                damage = curHp - 1;
                if (type == 1 || type == 2) {
                    MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                    service.interruptMobStatus(map.getId(), status);
                }
            }
            if (damage > 0) {
                lockMonster();
                try {
                    applyDamage(chr, damage, true, false);
                } finally {
                    unlockMonster();
                }

                if (type == 1) {
                    map.broadcastMessage(PacketCreator.damageMonster(getObjectId(), damage), getPosition());
                } else if (type == 2) {
                    if (damage < dealDamage) {    // ninja ambush (type 2) is already displaying DOT to the caster
                        map.broadcastMessage(PacketCreator.damageMonster(getObjectId(), damage), getPosition());
                    }
                }
            }
        }
    }

    /**
     * 获取名称。
     * @return String 类型结果
     */
    public String getName() {
        return stats.getName();
    }

    /**
     * 添加Stolen。
     * @param itemId 物品 ID
     */
    public void addStolen(int itemId) {
        stolenItems.add(itemId);
    }

    /**
     * 获取Stolen。
     * @return List<Integer> 类型结果
     */
    public List<Integer> getStolen() {
        return stolenItems;
    }

    /**
     * 设置Temp、克制。
     * @param e e
     * @param ee ee
     * @param milli milli
     */
    public void setTempEffectiveness(Element e, ElementalEffectiveness ee, long milli) {
        monsterLock.lock();
        try {
            final Element fE = e;
            final ElementalEffectiveness fEE = stats.getEffectiveness(e);
            if (!fEE.equals(ElementalEffectiveness.WEAK)) {
                stats.setEffectiveness(e, ee);

                MapleMap mmap = this.getMap();
                Runnable r = () -> {
                    monsterLock.lock();
                    try {
                        stats.removeEffectiveness(fE);
                        stats.setEffectiveness(fE, fEE);
                    } finally {
                        monsterLock.unlock();
                    }
                };

                MobClearSkillService service = (MobClearSkillService) mmap.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
                service.registerMobClearSkillAction(mmap.getId(), r, milli);
            }
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 执行 already、Buffed、属性 操作。
     * @return Collection<MonsterStatus> 类型结果
     */
    public Collection<MonsterStatus> alreadyBuffedStats() {
        statiLock.lock();
        try {
            return Collections.unmodifiableCollection(alreadyBuffed);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 获取Banish。
     * @return BanishInfo 类型结果
     */
    public BanishInfo getBanish() {
        return stats.getBanishInfo();
    }

    /**
     * 设置Boss。
     * @param boss boss
     */
    public void setBoss(boolean boss) {
        this.stats.setBoss(boss);
    }

    /**
     * 获取掉落、Period、时间。
     * @return int 类型结果
     */
    public int getDropPeriodTime() {
        return stats.getDropPeriod();
    }

    /**
     * 获取PA伤害。
     * @return int 类型结果
     */
    public int getPADamage() {
        return stats.getPADamage();
    }

    /**
     * 获取Stati。
     * @return Map<MonsterStatus, MonsterStatusEffect> 类型结果
     */
    public Map<MonsterStatus, MonsterStatusEffect> getStati() {
        statiLock.lock();
        try {
            return new HashMap<>(stati);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 获取Stati。
     * @param ms ms
     * @return MonsterStatusEffect 类型结果
     */
    public MonsterStatusEffect getStati(MonsterStatus ms) {
        statiLock.lock();
        try {
            return stati.get(ms);
        } finally {
            statiLock.unlock();
        }
    }

    // ---- one can always have fun trying these pieces of codes below in-game rofl ----

    /**
     * 获取Changed、属性。
     * @return ChangeableStats 类型结果
     */
    public final ChangeableStats getChangedStats() {
        return ostats;
    }

    /**
     * 获取怪物MaxHP。
     * @return int 类型结果
     */
    public final int getMobMaxHp() {
        if (ostats != null) {
            return ostats.hp;
        }
        return stats.getHp();
    }

    /**
     * 设置覆盖属性。
     * @param ostats ostats
     */
    public final void setOverrideStats(final OverrideMonsterStats ostats) {
        this.ostats = new ChangeableStats(stats, ostats);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    /**
     * 执行 change、等级 操作。
     * @param newLevel newLevel
     */
    public final void changeLevel(final int newLevel) {
        changeLevel(newLevel, true);
    }

    /**
     * 执行 change、等级 操作。
     * @param newLevel newLevel
     * @param pqMob pqMob
     */
    public final void changeLevel(final int newLevel, boolean pqMob) {
        if (!stats.isChangeable()) {
            return;
        }
        this.ostats = new ChangeableStats(stats, newLevel, pqMob);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    private float getDifficultyRate(final int difficulty) {
        switch (difficulty) {
            case 6:
                return (7.7f);
            case 5:
                return (5.6f);
            case 4:
                return (3.2f);
            case 3:
                return (2.1f);
            case 2:
                return (1.4f);
        }

        return (1.0f);
    }

    private void changeLevelByDifficulty(final int difficulty, boolean pqMob) {
        changeLevel((int) (this.getLevel() * getDifficultyRate(difficulty)), pqMob);
    }

    /**
     * 执行 change、Difficulty 操作。
     * @param difficulty difficulty
     * @param pqMob pqMob
     */
    public final void changeDifficulty(final int difficulty, boolean pqMob) {
        changeLevelByDifficulty(difficulty, pqMob);
    }

    // ---------------------------------------------------------------------------------

    private boolean isPuppetInVicinity(Summon summon) {
        return summon.getPosition().distanceSq(this.getPosition()) < 177777;
    }

    /**
     * 判断是否为角色、Puppet、在、Vicinity。
     * @param chr 角色
     * @return boolean 类型结果
     */
    public boolean isCharacterPuppetInVicinity(Character chr) {
        StatEffect mse = chr.getBuffEffect(BuffStat.PUPPET);
        if (mse != null) {
            Summon summon = chr.getSummonByKey(mse.getSourceId());

            // check whether mob is currently under a puppet's field of action or not
            if (summon != null) {
                return isPuppetInVicinity(summon);
            } else {
                map.getAggroCoordinator().removePuppetAggro(chr.getId());
            }
        }

        return false;
    }

    /**
     * 判断是否为Leading、Puppet、在、Vicinity。
     * @return boolean 类型结果
     */
    public boolean isLeadingPuppetInVicinity() {
        Character chrController = this.getActiveController();

        if (chrController != null) {
            return this.isCharacterPuppetInVicinity(chrController);
        }

        return false;
    }

    private Character getNextControllerCandidate() {
        int mincontrolled = Integer.MAX_VALUE;
        Character newController = null;

        int mincontrolleddead = Integer.MAX_VALUE;
        Character newControllerDead = null;

        Character newControllerWithPuppet = null;

        for (Character chr : getMap().getAllPlayers()) {
            if (!chr.isHidden()) {
                int ctrlMonsSize = chr.getNumControlledMonsters();

                if (isCharacterPuppetInVicinity(chr)) {
                    newControllerWithPuppet = chr;
                    break;
                } else if (chr.isAlive()) {
                    if (ctrlMonsSize < mincontrolled) {
                        mincontrolled = ctrlMonsSize;
                        newController = chr;
                    }
                } else {
                    if (ctrlMonsSize < mincontrolleddead) {
                        mincontrolleddead = ctrlMonsSize;
                        newControllerDead = chr;
                    }
                }
            }
        }

        if (newControllerWithPuppet != null) {
            return newControllerWithPuppet;
        } else if (newController != null) {
            return newController;
        } else {
            return newControllerDead;
        }
    }

    /**
     * 执行 aggro、移除、控制器 操作。
     * @return Pair<Character, Boolean> 类型结果
     */
    public Pair<Character, Boolean> aggroRemoveController() {
        Character chrController;
        boolean hadAggro;

        aggroUpdateLock.lock();
        try {
            chrController = getActiveController();
            hadAggro = isControllerHasAggro();

            this.setController(null);
            this.setControllerHasAggro(false);
            this.setControllerKnowsAboutAggro(false);
        } finally {
            aggroUpdateLock.unlock();
        }

        if (chrController != null) { // this can/should only happen when a hidden gm attacks the monster
            if (!this.isFake()) {
                chrController.sendPacket(PacketCreator.stopControllingMonster(this.getObjectId()));
            }
            chrController.stopControllingMonster(this);
        }

        return new Pair<>(chrController, hadAggro);
    }

    /**
     * 执行 aggro、Switch、控制器 操作。
     * @param newController newController
     * @param immediateAggro immediateAggro
     */
    public void aggroSwitchController(Character newController, boolean immediateAggro) {
        if (aggroUpdateLock.tryLock()) {
            try {
                Character prevController = getController();
                if (prevController == newController) {
                    return;
                }

                aggroRemoveController();
                if (!(newController != null && newController.isLoggedInWorld() && newController.getMap() == this.getMap())) {
                    return;
                }

                this.setController(newController);
                this.setControllerHasAggro(immediateAggro);
                this.setControllerKnowsAboutAggro(false);
                this.setControllerHasPuppet(false);
            } finally {
                aggroUpdateLock.unlock();
            }

            this.aggroUpdatePuppetVisibility();
            aggroMonsterControl(newController.getClient(), this, immediateAggro);
            newController.controlMonster(this);
        }
    }

    /**
     * 执行 aggro、添加、Puppet 操作。
     * @param player 玩家
     */
    public void aggroAddPuppet(Character player) {
        MonsterAggroCoordinator mmac = map.getAggroCoordinator();
        mmac.addPuppetAggro(player);

        aggroUpdatePuppetController(player);

        if (this.isControllerHasAggro()) {
            this.aggroUpdatePuppetVisibility();
        }
    }

    /**
     * 执行 aggro、移除、Puppet 操作。
     * @param player 玩家
     */
    public void aggroRemovePuppet(Character player) {
        MonsterAggroCoordinator mmac = map.getAggroCoordinator();
        mmac.removePuppetAggro(player.getId());

        aggroUpdatePuppetController(null);

        if (this.isControllerHasAggro()) {
            this.aggroUpdatePuppetVisibility();
        }
    }

    /**
     * 执行 aggro、更新、控制器 操作。
     */
    public void aggroUpdateController() {
        Character chrController = this.getActiveController();
        if (chrController != null && chrController.isAlive()) {
            return;
        }

        Character newController = getNextControllerCandidate();
        if (newController == null) {    // was a new controller found? (if not no one is on the map)
            return;
        }

        this.aggroSwitchController(newController, false);
    }

    /**
     * Finds a new controller for the given monster from the chars with deployed
     * puppet nearby on the map it is from...
     */
    private void aggroUpdatePuppetController(Character newController) {
        Character chrController = this.getActiveController();
        boolean updateController = false;

        if (chrController != null && chrController.isAlive()) {
            if (isCharacterPuppetInVicinity(chrController)) {
                return;
            }
        } else {
            updateController = true;
        }

        if (newController == null || !isCharacterPuppetInVicinity(newController)) {
            MonsterAggroCoordinator mmac = map.getAggroCoordinator();

            List<Integer> puppetOwners = mmac.getPuppetAggroList();
            List<Integer> toRemovePuppets = new LinkedList<>();

            for (Integer cid : puppetOwners) {
                Character chr = map.getCharacterById(cid);

                if (chr != null) {
                    if (isCharacterPuppetInVicinity(chr)) {
                        newController = chr;
                        break;
                    }
                } else {
                    toRemovePuppets.add(cid);
                }
            }

            for (Integer cid : toRemovePuppets) {
                mmac.removePuppetAggro(cid);
            }

            if (newController == null) {    // was a new controller found? (if not there's no puppet nearby)
                if (updateController) {
                    aggroUpdateController();
                }

                return;
            }
        } else if (chrController == newController) {
            this.aggroUpdatePuppetVisibility();
        }

        this.aggroSwitchController(newController, this.isControllerHasAggro());
    }

    /**
     * 执行 aggro、Redirect、控制器 操作。
     */
    public void aggroRedirectController() {
        this.aggroRemoveController();   // don't care if new controller not found, at least remove current controller
        this.aggroUpdateController();
    }

    /**
     * 执行 aggro、Move、生命体、更新 操作。
     * @param player 玩家
     * @return Boolean 类型结果
     */
    public Boolean aggroMoveLifeUpdate(Character player) {
        Character chrController = getController();
        if (chrController != null && player.getId() == chrController.getId()) {
            boolean aggro = this.isControllerHasAggro();
            if (aggro) {
                this.setControllerKnowsAboutAggro(true);
            }

            return aggro;
        } else {
            return null;
        }
    }

    /**
     * 执行 aggro、Auto、仇恨、更新 操作。
     * @param player 玩家
     */
    public void aggroAutoAggroUpdate(Character player) {
        Character chrController = this.getActiveController();

        if (chrController == null) {
            this.aggroSwitchController(player, true);
        } else if (chrController.getId() == player.getId()) {
            this.setControllerHasAggro(true);
            if (!GameConfig.getServerBoolean("use_auto_aggro_nearby")) {   // thanks Lichtmager for noticing autoaggro not updating the player properly
                aggroMonsterControl(player.getClient(), this, true);
            }
        }
    }

    /**
     * 执行 aggro、怪物、伤害 操作。
     * @param attacker attacker
     * @param damage 伤害值
     */
    public void aggroMonsterDamage(Character attacker, int damage) {
        MonsterAggroCoordinator mmac = this.getMapAggroCoordinator();
        mmac.addAggroDamage(this, attacker.getId(), damage);

        Character chrController = this.getController();    // aggro based on DPS rather than first-come-first-served, now live after suggestions thanks to MedicOP, Thora, Vcoc
        if (chrController != attacker) {
            if (this.getMapAggroCoordinator().isLeadingCharacterAggro(this, attacker)) {
                this.aggroSwitchController(attacker, true);
            } else {
                this.setControllerHasAggro(true);
                this.aggroUpdatePuppetVisibility();
            }
            
            /*
            For some reason, some mobs loses aggro on controllers if other players also attacks them.
            Maybe Nexon intended to interchange controllers at every attack...
            
            else if (chrController != null) {
                chrController.sendPacket(PacketCreator.stopControllingMonster(this.getObjectId()));
                aggroMonsterControl(chrController.getClient(), this, true);
            }
            */
        } else {
            this.setControllerHasAggro(true);
            this.aggroUpdatePuppetVisibility();
        }
    }

    private static void aggroMonsterControl(Client c, Monster mob, boolean immediateAggro) {
        c.sendPacket(PacketCreator.controlMonster(mob, false, immediateAggro));
    }

    private void aggroRefreshPuppetVisibility(Character chrController, Summon puppet) {
        // lame patch for client to redirect all aggro to the puppet

        List<Monster> puppetControlled = new LinkedList<>();
        for (Monster mob : chrController.getControlledMonsters()) {
            if (mob.isPuppetInVicinity(puppet)) {
                puppetControlled.add(mob);
            }
        }

        for (Monster mob : puppetControlled) {
            chrController.sendPacket(PacketCreator.stopControllingMonster(mob.getObjectId()));
        }
        chrController.sendPacket(PacketCreator.removeSummon(puppet, false));

        Client c = chrController.getClient();
        for (Monster mob : puppetControlled) { // thanks BHB for noticing puppets disrupting mobstatuses for bowmans
            aggroMonsterControl(c, mob, mob.isControllerKnowsAboutAggro());
        }
        chrController.sendPacket(PacketCreator.spawnSummon(puppet, false));
    }

    /**
     * 执行 aggro、更新、Puppet、Visibility 操作。
     */
    public void aggroUpdatePuppetVisibility() {
        if (!availablePuppetUpdate) {
            return;
        }

        availablePuppetUpdate = false;
        Runnable r = () -> {
            try {
                Character chrController = Monster.this.getActiveController();
                if (chrController == null) {
                    return;
                }

                StatEffect puppetEffect = chrController.getBuffEffect(BuffStat.PUPPET);
                if (puppetEffect != null) {
                    Summon puppet = chrController.getSummonByKey(puppetEffect.getSourceId());

                    if (puppet != null && isPuppetInVicinity(puppet)) {
                        controllerHasPuppet = true;
                        aggroRefreshPuppetVisibility(chrController, puppet);
                        return;
                    }
                }

                if (controllerHasPuppet) {
                    controllerHasPuppet = false;

                    chrController.sendPacket(PacketCreator.stopControllingMonster(Monster.this.getObjectId()));
                    aggroMonsterControl(chrController.getClient(), Monster.this, Monster.this.isControllerHasAggro());
                }
            } finally {
                availablePuppetUpdate = true;
            }
        };

        // had to schedule this since mob wouldn't stick to puppet aggro who knows why
        OverallService service = (OverallService) this.getMap().getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(this.getMap().getId(), r, GameConfig.getServerLong("update_interval"));
    }

    /**
     * 执行 aggro、Clear、Damages 操作。
     */
    public void aggroClearDamages() {
        this.getMapAggroCoordinator().removeAggroEntries(this);
    }

    /**
     * 执行 aggro、Reset、仇恨 操作。
     */
    public void aggroResetAggro() {
        aggroUpdateLock.lock();
        try {
            this.setControllerHasAggro(false);
            this.setControllerKnowsAboutAggro(false);
        } finally {
            aggroUpdateLock.unlock();
        }
    }

    /**
     * 获取移除、After。
     * @return int 类型结果
     */
    public final int getRemoveAfter() {
        return stats.removeAfter();
    }

    /**
     * 执行 dispose 操作。
     */
    public void dispose() {
        if (monsterItemDrop != null) {
            monsterItemDrop.cancel(false);
        }

        this.getMap().dismissRemoveAfter(this);
    }
}
