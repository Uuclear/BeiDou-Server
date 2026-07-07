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
package org.gms.server;

/**
 * TimerManager 的 JMX 管理接口，暴露活动线程数、队列任务数等运行时指标。
 */
public interface TimerManagerMBean {
    boolean isTerminated();
    /**
     * 调度器是否已关闭。
     * @return boolean
     */
    boolean isShutdown();
    /**
     * 获取已完成任务数。
     * @return long
     */
    long getCompletedTaskCount();
    /**
     * 获取当前活动线程数。
     * @return long
     */
    long getActiveCount();
    /**
     * 获取已提交任务总数。
     * @return long
     */
    long getTaskCount();
    /**
     * 获取队列中等待的任务数。
     * @return int
     */
    int getQueuedTasks();
}
