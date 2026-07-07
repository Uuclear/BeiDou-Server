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

import lombok.Getter;
import org.gms.net.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 全局定时任务管理器（单例），基于 ScheduledThreadPoolExecutor 调度周期性/延迟任务，并注册 JMX MBean 供监控。
 */
public class TimerManager implements TimerManagerMBean {
    private static final Logger log = LoggerFactory.getLogger(TimerManager.class);
    @Getter
    private static final TimerManager instance = new TimerManager();

    private ScheduledThreadPoolExecutor ses;

    private TimerManager() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(this, new ObjectName("server:type=TimerManger"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 4 线程的 ScheduledThreadPoolExecutor 并启动定时调度。
     */
    public void start() {
        if (ses != null && !ses.isShutdown() && !ses.isTerminated()) {
            return;
        }
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("TimerManager-Worker-" + threadNumber.getAndIncrement());
                return t;
            }
        });
        // 关闭 shutdown 后继续执行周期性任务的策略
        stpe.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        stpe.setRemoveOnCancelPolicy(true);

        stpe.setKeepAliveTime(5, MINUTES);
        stpe.allowCoreThreadTimeOut(true);

        ses = stpe;
    }

    /**
     * 立即停止调度线程池中的所有定时任务。
     */
    public void stop() {
        ses.shutdownNow();
    }

    /**
     * 返回用于清理已取消任务并刷新服务器时间的 Runnable。
     * @return Runnable 类型结果
     */
    public Runnable purge() {//Yay?
        return () -> {
            Server.getInstance().forceUpdateCurrentTime();
            ses.purge();
        };
    }

    /**
     * 注册周期性定时任务。
     * @param r Runnable 任务
     * @param repeatTime 重复间隔（毫秒）
     * @param delay 延迟（毫秒）
     * @return ScheduledFuture<?> 类型结果
     */
    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        return ses.scheduleAtFixedRate(new TimerRunner(r), delay, repeatTime, MILLISECONDS);
    }

    /**
     * 注册周期性定时任务。
     * @param r Runnable 任务
     * @param repeatTime 重复间隔（毫秒）
     * @return ScheduledFuture<?> 类型结果
     */
    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        return ses.scheduleAtFixedRate(new TimerRunner(r), 0, repeatTime, MILLISECONDS);
    }

    /**
     * 停止旧任务并以相同周期重新注册新任务。
     * @param sf ScheduledFuture
     * @param r Runnable 任务
     * @param repeatTime 重复间隔（毫秒）
     * @return ScheduledFuture<?> 类型结果
     */
    public ScheduledFuture<?> update(ScheduledFuture<?> sf, Runnable r, long repeatTime) {
       stop(sf);
        return ses.scheduleAtFixedRate(new TimerRunner(r), 0, repeatTime, MILLISECONDS);
    }

    /**
     * 取消指定的 ScheduledFuture 任务。
     * @param sf ScheduledFuture
     */
    public void stop(ScheduledFuture<?> sf) {
        if (sf != null && !sf.isCancelled()) {
            sf.cancel(false);
        }
    }

    /**
     * 注册一次性延迟任务。
     * @param r Runnable 任务
     * @param delay 延迟（毫秒）
     * @return ScheduledFuture<?> 类型结果
     */
    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        return ses.schedule(new TimerRunner(r), delay, MILLISECONDS);
    }

    /**
     * 在指定时间戳执行任务。
     * @param r Runnable 任务
     * @param timestamp 目标时间戳
     * @return ScheduledFuture<?> 类型结果
     */
    public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
        return schedule(r, timestamp - System.currentTimeMillis());
    }

    /**
     * 获取当前正在执行的任务线程数。
     * @return 活动线程数
     */
    @Override
    public long getActiveCount() {
        return ses.getActiveCount();
    }

    /**
     * 获取已完成的任务总数。
     * @return 已完成任务数
     */
    @Override
    public long getCompletedTaskCount() {
        return ses.getCompletedTaskCount();
    }

    /**
     * 获取等待队列中的任务数量。
     * @return 排队任务数
     */
    @Override
    public int getQueuedTasks() {
        return ses.getQueue().toArray().length;
    }

    /**
     * 获取已提交的任务总数（含已完成）。
     * @return 任务总数
     */
    @Override
    public long getTaskCount() {
        return ses.getTaskCount();
    }

    /**
     * 判断调度器是否已关闭。
     * @return 若已关闭返回 true
     */
    @Override
    public boolean isShutdown() {
        return ses.isShutdown();
    }

    /**
     * 判断调度器是否已完全终止。
     * @return boolean 类型结果
     */
    public boolean isTerminated() {
        return ses.isTerminated();
    }


    private static class TimerRunner implements Runnable {
        Runnable r;

        public TimerRunner(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            // 捕获定时任务异常，避免单个任务失败导致调度器线程终止
            try {
                r.run();
            } catch (Throwable t) {
                log.error("Error in scheduled task", t);
            }
        }
    }
}
