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
package org.gms.net.server.guild;

/**
 * 公会摘要信息，用于在地图上展示公会名称与徽章等简要数据。
 */
public class GuildSummary {
    private final String name;
    private final short logoBG;
    private final byte logoBGColor;
    private final short logo;
    private final byte logoColor;
    private final int allianceId;

    /**
     * 从公会实体构造摘要信息。
     *
     * @param g 公会实体
     */
    public GuildSummary(Guild g) {
        this.name = g.getName();
        this.logoBG = (short) g.getLogoBG();
        this.logoBGColor = (byte) g.getLogoBGColor();
        this.logo = (short) g.getLogo();
        this.logoColor = (byte) g.getLogoColor();
        this.allianceId = g.getAllianceId();
    }

    /** 返回公会名称。 */
    public String getName() {
        return name;
    }

    /** 返回徽章背景图案 ID。 */
    public short getLogoBG() {
        return logoBG;
    }

    /** 返回徽章背景颜色。 */
    public byte getLogoBGColor() {
        return logoBGColor;
    }

    /** 返回徽章图案 ID。 */
    public short getLogo() {
        return logo;
    }

    /** 返回徽章图案颜色。 */
    public byte getLogoColor() {
        return logoColor;
    }

    /** 返回所属联盟 ID。 */
    public int getAllianceId() {
        return allianceId;
    }
}
