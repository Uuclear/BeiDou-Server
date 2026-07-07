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
package org.gms.util;

import org.gms.dao.mapper.PetsMapper;
import org.gms.dao.mapper.RingsMapper;
import org.gms.manager.ServerManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 现金商城相关唯一 ID 生成器（戒指、宠物等），避免与数据库已有 ID 冲突。
 * <p>
 * 从 rings、pets 表加载已占用 ID，内存递增分配；接近上限时重新从数据库同步。
 *
 * @author RonanLana
 */
public class CashIdGenerator {
    private final static Set<Integer> existentCashIds = new HashSet<>(10000);
    private static Integer runningCashId = 0;

    /**
     * 从数据库加载已占用的现金 ID（戒指、宠物），并重置内存游标至下一个可用值。
     */
    public static synchronized void loadExistentCashIdsFromDb() {
        RingsMapper ringsMapper = ServerManager.getApplicationContext().getBean(RingsMapper.class);
        existentCashIds.clear();
        ringsMapper.selectAll().forEach(ringsDO -> {
            if (ringsDO.getId() != null) {
                existentCashIds.add(ringsDO.getId());
            }
        });
        PetsMapper petsMapper = ServerManager.getApplicationContext().getBean(PetsMapper.class);
        petsMapper.selectAll().forEach(petsDO -> {
            if (petsDO.getPetid() != null) {
                existentCashIds.add(petsDO.getPetid().intValue());
            }
        });

        runningCashId = 0;
        do {
            runningCashId++;    // hopefully the id will never surpass the allotted amount for pets/rings?
        } while (existentCashIds.contains(runningCashId));
    }

    private static void getNextAvailableCashId() {
        runningCashId++;
        if (runningCashId >= 777000000) {
            loadExistentCashIdsFromDb();
        }
    }

    /**
     * 生成下一个未占用的现金商城唯一 ID。
     *
     * @return 新的现金 ID
     */
    public static synchronized int generateCashId() {
        while (true) {
            if (!existentCashIds.contains(runningCashId)) {
                int ret = runningCashId;
                getNextAvailableCashId();

                // existentCashids.add(ret)... no need to do this since the wrap over already refetches already used cashids from the DB
                return ret;
            }

            getNextAvailableCashId();
        }
    }

    /**
     * 释放已分配的现金 ID，使其可被再次分配（例如物品销毁时）。
     *
     * @param cashId 待释放的 ID
     */
    public static synchronized void freeCashId(int cashId) {
        existentCashIds.remove(cashId);
    }

}
