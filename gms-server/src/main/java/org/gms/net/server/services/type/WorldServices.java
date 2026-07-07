/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
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
package org.gms.net.server.services.type;

import org.gms.net.server.services.BaseService;
import org.gms.net.server.services.Service;
import org.gms.net.server.services.ServiceType;
import org.gms.net.server.services.task.world.CharacterSaveService;

/**
 * 世界级服务类型枚举，当前提供角色存档调度服务。
 *
 * @author Ronan
 */
public enum WorldServices implements ServiceType {

    SAVE_CHARACTER(CharacterSaveService.class);

    private final Class<? extends BaseService> s;

    WorldServices(Class<? extends BaseService> service) {
        s = service;
    }

    @Override
    public Service createService() {
        return new Service(s);
    }

    @Override
    public WorldServices[] enumValues() {
        return WorldServices.values();
    }

}
