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
/**
 * 家族系统管理类，维护家族成员关系、声望及特权。
 */
public class Family {
    private static final Logger log = LoggerFactory.getLogger(Family.class);
    private static final AtomicInteger familyIDCounter = new AtomicInteger();

    private final int id, world;
    private final Map<Integer, FamilyEntry> members = new ConcurrentHashMap<>();
    private FamilyEntry leader;
    private String name;
    private String preceptsMessage = "";
    private int totalGenerations;

    /**
     * 家族
     * @param id ID
     * @param world 世界
     */
    public Family(int id, int world) {
        int newId = id;
        if (id == -1) {
            // get next available family id
            while (idInUse(newId = familyIDCounter.incrementAndGet())) {
            }
        }
        this.id = newId;
        this.world = world;
    }

    private static boolean idInUse(int id) {
        for (World world : Server.getInstance().getWorlds()) {
            if (world.getFamily(id) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取ID
     * @return 返回值
     */
    public int getID() {
        return id;
    }

    /**
     * 获取世界
     * @return 返回值
     */
    public int getWorld() {
        return world;
    }

    /**
     * 设置Leader
     * @param leader leader
     */
    public void setLeader(FamilyEntry leader) {
        this.leader = leader;
        setName(leader.getName());
    }

    /**
     * 获取Leader
     * @return 返回值
     */
    public FamilyEntry getLeader() {
        return leader;
    }

    private void setName(String name) {
        this.name = name;
    }

    /**
     * 获取TotalMembers
     * @return 返回值
     */
    public int getTotalMembers() {
        return members.size();
    }

    /**
     * 获取TotalGenerations
     * @return 返回值
     */
    public int getTotalGenerations() {
        return totalGenerations;
    }

    /**
     * 设置TotalGenerations
     * @param generations generations
     */
    public void setTotalGenerations(int generations) {
        this.totalGenerations = generations;
    }

    /**
     * 获取名称
     * @return 返回值
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置Message
     * @param message 消息
     * @param save save
     */
    public void setMessage(String message, boolean save) {
        this.preceptsMessage = message;
        if (save) {
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("UPDATE family_character SET precepts = ? WHERE cid = ?")) {
                ps.setString(1, message);
                ps.setInt(2, getLeader().getChrId());
                ps.executeUpdate();
            } catch (SQLException e) {
                log.error("Could not save new precepts for family {}", getID(), e);
            }
        }
    }

    /**
     * 获取Message
     * @return 返回值
     */
    public String getMessage() {
        return preceptsMessage;
    }

    /**
     * 添加条目
     * @param entry entry
     */
    public void addEntry(FamilyEntry entry) {
        members.put(entry.getChrId(), entry);
    }

    /**
     * 移除条目Branch
     * @param root root
     */
    public void removeEntryBranch(FamilyEntry root) {
        members.remove(root.getChrId());
        for (FamilyEntry junior : root.getJuniors()) {
            if (junior != null) {
                removeEntryBranch(junior);
            }
        }
    }

    /**
     * 添加条目Tree
     * @param root root
     */
    public void addEntryTree(FamilyEntry root) {
        members.put(root.getChrId(), root);
        for (FamilyEntry junior : root.getJuniors()) {
            if (junior != null) {
                addEntryTree(junior);
            }
        }
    }

    /**
     * 获取条目按ID
     * @param cid cid
     * @return 返回值
     */
    public FamilyEntry getEntryByID(int cid) {
        return members.get(cid);
    }

    /**
     * 广播
     * @param packet 封包
     */
    public void broadcast(Packet packet) {
        broadcast(packet, -1);
    }

    /**
     * 广播
     * @param packet 封包
     * @param ignoreID ignoreID
     */
    public void broadcast(Packet packet, int ignoreID) {
        for (FamilyEntry entry : members.values()) {
            Character chr = entry.getChr();
            if (chr != null) {
                if (chr.getId() == ignoreID) {
                    continue;
                }
                chr.sendPacket(packet);
            }
        }
    }

    /**
     * Familybuff
     * @param duration duration
     */
    /**
     * Familybuff
     * @param duration duration
     */
    /**
     * 激活家族增益效果
     * @param duration 持续时间
     */
    public void Familybuff(int duration) {
        for (FamilyEntry entry : members.values()) {
            Character chr = entry.getChr();
            if (chr != null) {
                chr.sendPacket(PacketCreator.familyBuff(4, 4, 1, duration  * 60000));
                chr.setFamilyBuff(true,2,2);
                chr.startFamilyBuffTimer(duration  * 60000);
            }
        }
    }

    /**
     * 广播家族信息Update
     */
    public void broadcastFamilyInfoUpdate() {
        for (FamilyEntry entry : members.values()) {
            Character chr = entry.getChr();
            if (chr != null) {
                chr.sendPacket(PacketCreator.getFamilyInfo(entry));
            }
        }
    }

    /**
     * 重置DailyReps
     */
    public void resetDailyReps() {
        for (FamilyEntry entry : members.values()) {
            entry.setTodaysRep(0);
            entry.setRepsToSenior(0);
            entry.resetEntitlementUsages();
        }
    }

    /**
     * 保存全部MembersRep
     */
    public void saveAllMembersRep() { //was used for autosave task, but character autosave should be enough
            con.setAutoCommit(false);
            boolean success = true;
            for (FamilyEntry entry : members.values()) {
                success = entry.saveReputation(con);
                if (!success) {
                    break;
                }
            }
            if (!success) {
                con.rollback();
                log.error("Family rep autosave failed for family {}", getID());
            }
            con.setAutoCommit(true);
            //reset repChanged after successful save
            for (FamilyEntry entry : members.values()) {
                entry.savedSuccessfully();
            }
        } catch (SQLException e) {
            log.error("Could not get connection to DB while saving all members rep", e);
        }
    }
}
