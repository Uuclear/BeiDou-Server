package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.client.Family;
import org.gms.client.FamilyEntry;
import org.gms.client.Job;
import org.gms.dao.entity.CharactersDO;
import org.gms.dao.entity.FamilyCharacterDO;
import org.gms.dao.entity.FamilyEntitlementDO;
import org.gms.dao.mapper.FamilyCharacterMapper;
import org.gms.dao.mapper.FamilyEntitlementMapper;
import org.gms.net.server.Server;
import org.gms.net.server.world.World;
import org.gms.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.gms.dao.entity.table.FamilyEntitlementDOTableDef.FAMILY_ENTITLEMENT_D_O;

/**
 * 家族服务类
 * 负责服务器启动时加载所有家族数据，包括家族成员、家族族长、长辈/晚辈关系、家族权限等。
 *
 * @author GMS Server
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class FamilyService {
    /** 家族成员数据访问接口 */
    private final FamilyCharacterMapper familyCharacterMapper;
    /** 家族权限数据访问接口 */
    private final FamilyEntitlementMapper familyEntitlementMapper;
    /** 角色服务 */
    private final CharacterService characterService;

    /**
     * 加载所有家族数据
     * 从数据库读取所有家族成员记录，构建家族对象，设置族长、成员关系、已使用权限等，
     * 处理未匹配的晚辈关联，最后统计每个家族的人数
     */
    public void loadAllFamilies() {
        List<FamilyCharacterDO> familyCharacterDOList = familyCharacterMapper.selectAll();
        List<Pair<Integer, FamilyEntry>> unmatchedJuniors = new ArrayList<>(); // <<world, seniorid> familyEntry>
        for (FamilyCharacterDO familyCharacterDO : familyCharacterDOList) {
            CharactersDO charactersDO = characterService.findById(familyCharacterDO.getCid());
            if (charactersDO == null) {
                continue;
            }
            World world = Server.getInstance().getWorld(charactersDO.getWorld());
            if (world == null) {
                continue;
            }
            Family family = world.getFamily(familyCharacterDO.getFamilyid());
            if (family == null) {
                family = new Family(familyCharacterDO.getFamilyid(), charactersDO.getWorld());
                world.addFamily(familyCharacterDO.getFamilyid(), family);
            }
            FamilyEntry familyEntry = new FamilyEntry(family, charactersDO.getId(), charactersDO.getName(),
                    charactersDO.getLevel(), Job.getById(charactersDO.getJob()));
            familyEntry.setReputation(familyCharacterDO.getReputation());
            familyEntry.setTodaysRep(familyCharacterDO.getTodaysrep());
            familyEntry.setTotalReputation(familyCharacterDO.getTotalreputation());
            familyEntry.setRepsToSenior(familyCharacterDO.getReptosenior());
            family.addEntry(familyEntry);
            if (familyCharacterDO.getSeniorid() <= 0) {
                family.setLeader(familyEntry);
                family.setMessage(familyCharacterDO.getPrecepts(), false);
            }
            FamilyEntry senior = family.getEntryByID(familyCharacterDO.getSeniorid());
            if (senior != null) {
                familyEntry.setSenior(senior, false);
            } else if (familyCharacterDO.getSeniorid() > 0) {
                unmatchedJuniors.add(new Pair<>(familyCharacterDO.getSeniorid(), familyEntry));
            }
            List<FamilyEntitlementDO> familyEntitlementDOList = familyEntitlementMapper.selectListByQuery(QueryWrapper.create()
                    .select(FAMILY_ENTITLEMENT_D_O.ENTITLEMENTID)
                    .from(FAMILY_ENTITLEMENT_D_O)
                    .where(FAMILY_ENTITLEMENT_D_O.CHARID.eq(charactersDO.getId())));
            familyEntitlementDOList.forEach(familyEntitlementDO -> familyEntry.setEntitlementUsed(familyEntitlementDO.getEntitlementid()));
        }
        for (Pair<Integer, FamilyEntry> unmatchedJunior : unmatchedJuniors) {
            FamilyEntry senior = Server.getInstance()
                    .getWorld(unmatchedJunior.getRight().getFamily().getWorld())
                    .getFamily(unmatchedJunior.getRight().getFamily().getID())
                    .getEntryByID(unmatchedJunior.getLeft());
            if (senior != null) {
                unmatchedJunior.getRight().setSenior(senior, false);
            }
        }
        for (World world : Server.getInstance().getWorlds()) {
            for (Family family : world.getFamilies()) {
                family.getLeader().doFullCount();
            }
        }
    }
}
