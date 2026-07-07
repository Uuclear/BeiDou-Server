package org.gms.client.inventory;

/**
 * 背包变更操作封装类，用于批量通知客户端背包物品的增删改。
 */
public class ModifyInventory {

    private final int mode;
    private Item item;
    private short oldPos;

    /**
     * 构造背包变更记录。
     *
     * @param mode 变更模式（增/删/改）
     * @param item 变更的物品
     */
    public ModifyInventory(final int mode, final Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    /**
     * 构造带原位置的背包变更记录（用于物品移动）。
     *
     * @param mode 变更模式
     * @param item 变更的物品
     * @param oldPos 物品原槽位
     */
    public ModifyInventory(final int mode, final Item item, final short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }

    /** @return 变更模式 */
    public final int getMode() {
        return mode;
    }

    /** @return 背包类型编号 */
    public final int getInventoryType() {
        return item.getInventoryType().getType();
    }

    /** @return 物品当前槽位 */
    public final short getPosition() {
        return item.getPosition();
    }

    /** @return 物品原槽位 */
    public final short getOldPosition() {
        return oldPos;
    }

    /** @return 物品数量 */
    public final short getQuantity() {
        return item.getQuantity();
    }

    /** @return 变更的物品实例 */
    public final Item getItem() {
        return item;
    }

    /** 清空持有的物品引用。 */
    public final void clear() {
        this.item = null;
    }
}