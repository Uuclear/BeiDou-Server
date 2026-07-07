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
package org.gms.scripting.event;

import org.gms.scripting.event.scheduler.EventScriptScheduler;

/**
 * 事件调度任务的取消句柄。
 * <p>
 * 由 {@link EventManager#schedule(String, long)} 等方法返回，
 * 调用 {@link #cancel(boolean)} 可从 {@link EventScriptScheduler} 中移除尚未执行的脚本回调。
 * 无论参数值如何，均不会在任务执行中强制中断（non-interrupt）。
 * </p>
 *
 * @author Ronan
 */
public class EventScheduledFuture {
    Runnable r;
    EventScriptScheduler ess;

    /**
     * @param r   已注册的脚本回调任务
     * @param ess 所属事件脚本调度器
     */
    public EventScheduledFuture(Runnable r, EventScriptScheduler ess) {
        this.r = r;
        this.ess = ess;
    }

    /**
     * 取消尚未执行的调度任务。
     *
     * @param dummy 保留参数，实际始终采用非中断取消策略
     */
    public void cancel(boolean dummy) {   // will always implement "non-interrupt if running" regardless of boolean value
        ess.cancelEntry(r);
    }
}
