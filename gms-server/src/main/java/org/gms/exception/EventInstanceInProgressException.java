/*
    This file is part of the HeavenMS Maple Story Server
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
 * 事件实例正在进行中异常
 * <p>
 * 当尝试启动一个已经在进行中的事件实例时抛出此异常。
 * </p>
 *
 * @author Ronan
 * @since 1.0.0
 */
public class EventInstanceInProgressException extends Exception {

    /**
     * 异常消息前缀常量
     */
    public static String EIIP_KEY = "Event instance ";

    /**
     * 构造函数
     *
     * @param eventName     事件名称
     * @param eventInstance 事件实例名称
     */
    public EventInstanceInProgressException(String eventName, String eventInstance) {
        super(EIIP_KEY + "already in progress - " + eventName + ", EM: " + eventInstance);
    }
}
