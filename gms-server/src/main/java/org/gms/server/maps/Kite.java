package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.packet.Packet;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 风筝道具地图展示对象。
 */
public class Kite extends AbstractMapObject {
    private final Point pos;
    private final Character owner;
    private final String text;
    private final int ft;
    private final int itemid;

    /**
     * 构造 Kite 实例。
     * @param owner 归属角色
     * @param text text
     * @param itemId 物品 ID
     */
    public Kite(Character owner, String text, int itemId) {
        this.owner = owner;
        this.pos = owner.getPosition();
        this.ft = owner.getFh();
        this.text = text;
        this.itemid = itemId;
    }

    /**
     * 获取类型。
     * @return MapObjectType 类型结果
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.KITE;
    }

    /**
     * 获取位置。
     * @return Point 类型结果
     */
    @Override
    public Point getPosition() {
        return pos.getLocation();
    }

    /**
     * 获取归属者。
     * @return Character 类型结果
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 设置位置。
     * @param position 坐标
     */
    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    /**
     * 执行 send、Destroy、数据 操作。
     * @param client client
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(makeDestroyData());
    }

    /**
     * 执行 send、刷新、数据 操作。
     * @param client client
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(makeSpawnData());
    }

    /**
     * 执行 make、刷新、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeSpawnData() {
        return PacketCreator.spawnKite(getObjectId(), itemid, owner.getName(), text, pos, ft);
    }

    /**
     * 执行 make、Destroy、数据 操作。
     * @return Packet 类型结果
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeKite(getObjectId(), 0);
    }
}