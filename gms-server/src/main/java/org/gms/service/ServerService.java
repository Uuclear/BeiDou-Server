package org.gms.service;

import org.gms.model.dto.ChannelListRtnDTO;
import org.gms.model.dto.WorldListRtnDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务器信息业务服务，提供大区列表、频道列表等运行时拓扑数据。
 */
@Service
public class ServerService {

    /**
     * 执行 worldList 相关业务逻辑。
     * @return List<WorldListRtnDTO> 类型结果
     */
    public List<WorldListRtnDTO> worldList() {
        List<World> worlds = Server.getInstance().getWorlds();
        return worlds.stream()
                .map(w -> WorldListRtnDTO.builder()
                        .id(w.getId())
                        .expRate(w.getExpRate())
                        .dropRate(w.getDropRate())
                        .mesoRate(w.getMesoRate())
                        .bossDropRate(w.getBossDropRate())
                        .questRate(w.getQuestRate())
                        .travelRate(w.getTravelRate())
                        .fishingRate(w.getFishingRate())
                        .build())
                .toList();
    }

    /**
     * 执行 channelList 相关业务逻辑。
     *
     * @param worldId 大区（世界）ID
     * @return List<ChannelListRtnDTO> 类型结果
     */
    public List<ChannelListRtnDTO> channelList(int worldId) {
        List<Channel> channels = Server.getInstance().getWorld(worldId).getChannels();
        return channels.stream()
                .map(c -> ChannelListRtnDTO.builder().id(c.getId()).worldId(c.getWorld()).build())
                .toList();
    }
}
