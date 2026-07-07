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

import org.gms.server.maps.AbstractAnimatedMapObject;

/**
 * 已加载生命体抽象基类（怪物与 NPC 的公共父类）。
 */
public abstract class AbstractLoadedLife extends AbstractAnimatedMapObject {
    private final int id;
    private int f;
    private boolean hide;
    private int fh;
    private int start_fh;
    private int cy;
    private int rx0;
    private int rx1;

    /**
     * 构造 AbstractLoadedLife 实例。
     * @param id ID
     */
    public AbstractLoadedLife(int id) {
        this.id = id;
    }

    /**
     * 构造 AbstractLoadedLife 实例。
     * @param life life
     */
    public AbstractLoadedLife(AbstractLoadedLife life) {
        this(life.getId());
        this.f = life.f;
        this.hide = life.hide;
        this.fh = life.fh;
        this.start_fh = life.fh;
        this.cy = life.cy;
        this.rx0 = life.rx0;
        this.rx1 = life.rx1;
    }

    /**
     * 获取F。
     * @return int 类型结果
     */
    public int getF() {
        return f;
    }

    /**
     * 设置F。
     * @param f f
     */
    public void setF(int f) {
        this.f = f;
    }

    /**
     * 判断是否为Hidden。
     * @return boolean 类型结果
     */
    public boolean isHidden() {
        return hide;
    }

    /**
     * 设置隐藏。
     * @param hide hide
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }

    /**
     * 获取Fh。
     * @return int 类型结果
     */
    public int getFh() {
        return fh;
    }

    /**
     * 设置Fh。
     * @param fh fh
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     * 获取Start、Fh。
     * @return int 类型结果
     */
    public int getStartFh() {
        return start_fh;
    }

    /**
     * 获取Cy。
     * @return int 类型结果
     */
    public int getCy() {
        return cy;
    }

    /**
     * 设置Cy。
     * @param cy cy
     */
    public void setCy(int cy) {
        this.cy = cy;
    }

    /**
     * 获取Rx0。
     * @return int 类型结果
     */
    public int getRx0() {
        return rx0;
    }

    /**
     * 设置Rx0。
     * @param rx0 rx0
     */
    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    /**
     * 获取Rx1。
     * @return int 类型结果
     */
    public int getRx1() {
        return rx1;
    }

    /**
     * 设置Rx1。
     * @param rx1 rx1
     */
    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    /**
     * 获取ID。
     * @return int 类型结果
     */
    public int getId() {
        return id;
    }
}
