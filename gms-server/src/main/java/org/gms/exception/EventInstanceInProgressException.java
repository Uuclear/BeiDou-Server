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
package org.gms.exception;


/**
 * 事件实例进行中异常，表示同一事件实例已在运行，禁止重复创建。
 * 源自 HeavenMS 事件系统，用于活动副本并发控制。
 */
public class EventInstanceInProgressException extends Exception {

    public static String EIIP_KEY = "Event instance ";

    /**
     * 构造 EventInstanceInProgressException。
     *
     * @param eventName 事件名称
     * @param eventInstance 事件管理器标识
     */
    public EventInstanceInProgressException(String eventName, String eventInstance) {
        super(EIIP_KEY + "already in progress - " + eventName + ", EM: " + eventInstance);
    }

}
