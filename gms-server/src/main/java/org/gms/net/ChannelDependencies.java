package org.gms.net;

import org.gms.client.processor.npc.FredrickProcessor;
import org.gms.service.NoteService;

import java.util.Objects;

/**
 * 频道服 {@link PacketHandler} 所需的外部服务依赖容器。
 * <p>
 * 部分频道处理器（如笔记、Fredrick 仓库、现金商店等）需要访问 Spring 管理的服务或处理器，
 * 通过此 record 在 {@link PacketProcessor#registerGameHandlerDependencies(ChannelDependencies)} 时注入，
 * 避免处理器直接耦合 {@link org.gms.net.server.Server} 单例。
 * </p>
 *
 * @param noteService        笔记/邮件服务
 * @param fredrickProcessor  Fredrick 仓库处理器
 */
public record ChannelDependencies(NoteService noteService, FredrickProcessor fredrickProcessor) {

    public ChannelDependencies {
        Objects.requireNonNull(noteService);
        Objects.requireNonNull(fredrickProcessor);
    }
}
