package org.gms.net;

import org.gms.client.processor.npc.FredrickProcessor;
import org.gms.service.NoteService;

import java.util.Objects;

/**
 * 频道服务器依赖记录类
 * 使用Java Record特性，封装频道服务器处理器所需的外部依赖服务
 * 用于依赖注入，避免处理器直接创建服务实例
 *
 * @param noteService          纸条服务，处理游戏内邮件/纸条功能
 * @param fredrickProcessor    弗雷德里克处理器，处理雇佣商人相关逻辑
 */
public record ChannelDependencies(NoteService noteService, FredrickProcessor fredrickProcessor) {

    /**
     * 紧凑构造函数，验证所有依赖不为null
     *
     * @throws NullPointerException 如果任何依赖为null则抛出异常
     */
    public ChannelDependencies {
        Objects.requireNonNull(noteService);
        Objects.requireNonNull(fredrickProcessor);
    }
}
