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
package org.gms.server.maps;

import org.gms.scripting.event.EventInstanceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 地图管理器，按频道/世界缓存并分发 MapleMap 实例。
 */
public class MapManager {
    private final int channel;
    private final int world;
    private EventInstanceManager event;

    private final Map<Integer, MapleMap> maps = new HashMap<>();

    private final Lock mapsRLock;
    private final Lock mapsWLock;

    /**
     * 构造 MapManager 实例。
     * @param eim 事件实例管理器
     * @param world world
     * @param channel 频道号
     */
    public MapManager(EventInstanceManager eim, int world, int channel) {
        this.world = world;
        this.channel = channel;
        this.event = eim;

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        this.mapsRLock = readWriteLock.readLock();
        this.mapsWLock = readWriteLock.writeLock();
    }

    /**
     * 重置地图。
     * @param mapid 地图 ID
     * @return MapleMap 类型结果
     */
    public MapleMap resetMap(int mapid) {
        mapsWLock.lock();
        try {
            maps.remove(mapid);
        } finally {
            mapsWLock.unlock();
        }

        return getMap(mapid);
    }

    private synchronized MapleMap loadMapFromWz(int mapid, boolean cache) {
        MapleMap map;

        if (cache) {
            mapsRLock.lock();
            try {
                map = maps.get(mapid);
            } finally {
                mapsRLock.unlock();
            }

            if (map != null) {
                return map;
            }
        }

        map = MapFactory.loadMapFromWz(mapid, world, channel, event);

        if (cache) {
            mapsWLock.lock();
            try {
                maps.put(mapid, map);
            } finally {
                mapsWLock.unlock();
            }
        }

        return map;
    }

    /**
     * 获取地图。
     * @param mapid 地图 ID
     * @return MapleMap 类型结果
     */
    public MapleMap getMap(int mapid) {
        MapleMap map;

        mapsRLock.lock();
        try {
            map = maps.get(mapid);
        } finally {
            mapsRLock.unlock();
        }

        return (map != null) ? map : loadMapFromWz(mapid, true);
    }

    /**
     * 获取地图按生命体ID。
     * @param lifeId lifeId
     * @return MapleMap 类型结果
     */
    public MapleMap getMapByLifeId(int lifeId) {
        String mapId = MapFactory.getMapIdByLifeId(lifeId);
        return mapId == null ? null : getMap(Integer.parseInt(mapId));
    }

    /**
     * 获取Disposable、地图。
     * @param mapid 地图 ID
     * @return MapleMap 类型结果
     */
    public MapleMap getDisposableMap(int mapid) {
        return loadMapFromWz(mapid, false);
    }

    /**
     * 判断是否为地图已加载。
     * @param mapId mapId
     * @return boolean 类型结果
     */
    public boolean isMapLoaded(int mapId) {
        mapsRLock.lock();
        try {
            return maps.containsKey(mapId);
        } finally {
            mapsRLock.unlock();
        }
    }

    /**
     * 获取Maps。
     * @return Map<Integer, MapleMap> 类型结果
     */
    public Map<Integer, MapleMap> getMaps() {
        mapsRLock.lock();
        try {
            return new HashMap<>(maps);
        } finally {
            mapsRLock.unlock();
        }
    }

    /**
     * 更新Maps。
     */
    public void updateMaps() {
        for (MapleMap map : getMaps().values()) {
            map.respawn();
            map.mobMpRecovery();
        }
    }

    /**
     * 执行 dispose 操作。
     */
    public void dispose() {
        for (MapleMap map : getMaps().values()) {
            map.dispose();
        }

        this.event = null;
    }

}
