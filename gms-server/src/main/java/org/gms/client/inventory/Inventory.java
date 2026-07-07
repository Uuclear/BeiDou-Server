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
/**
 * 背包管理类，管理某一类型背包栏的物品存取、排序、扩容及槽位限制。
 */
public class Inventory implements Iterable<Item> {
    private static final Logger log = LoggerFactory.getLogger(Inventory.class);
    protected final Map<Short, Item> inventory;
    protected final InventoryType type;
    protected final Lock lock = new ReentrantLock(true);

    protected Character owner;
    protected byte slotLimit;
    protected boolean checked = false;

    /**
     * 背包
     * @param mc mc
     * @param type 类型
     * @param slotLimit slotLimit
     */
    public Inventory(Character mc, InventoryType type, byte slotLimit) {
        this.owner = mc;
        this.inventory = new LinkedHashMap<>();
        this.type = type;
        this.slotLimit = slotLimit;
    }

    /**
     * 判断是否为Extendable背包
     * @return 返回值
     */
    public boolean isExtendableInventory() { // not sure about cash, basing this on the previous one.
    }

    /**
     * 判断是否为Equip背包
     * @return 返回值
     */
    public boolean isEquipInventory() {
        return type.equals(InventoryType.EQUIP) || type.equals(InventoryType.EQUIPPED);
    }

    /**
     * 获取槽位上限
     * @return 返回值
     */
    public byte getSlotLimit() {
        lock.lock();
        try {
            return slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置槽位上限
     * @param newLimit newLimit
     */
    public void setSlotLimit(int newLimit) {
        lock.lock();
        try {
            if (newLimit < slotLimit) {
                List<Short> toRemove = new LinkedList<>();
                for (Item it : list()) {
                    if (it.getPosition() > newLimit) {
                        toRemove.add(it.getPosition());
                    }
                }

                for (Short slot : toRemove) {
                    removeSlot(slot);
                }
            }

            slotLimit = (byte) newLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * list
     * @return 返回值
     */
    /**
     * list
     * @return 返回值
     */
    public Collection<Item> list() {
        lock.lock();
        try {
            return new ArrayList<>(inventory.values());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 查找按ID
     * @param itemId 物品ID
     * @return 返回值
     */
    public Item findById(int itemId) {
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 查找按名称
     * @param name 名称
     * @return 返回值
     */
    public Item findByName(String name) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        for (Item item : list()) {
            String itemName = ii.getName(item.getItemId());
            if (itemName == null) {
                log.error("[CRITICAL] Item {} has no name", item.getItemId());
                continue;
            }

            if (name.compareToIgnoreCase(itemName) == 0) {
                return item;
            }
        }
        return null;
    }

    /**
     * count按ID
     * @param itemId 物品ID
     * @return 返回值
     */
    public int countById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * countNotOwned按ID
     * @param itemId 物品ID
     * @return 返回值
     */
    public int countNotOwnedById(int itemId) {
        int qty = 0;
        for (Item item : list()) {
            if (item.getItemId() == itemId && item.getOwner().equals("")) {
                qty += item.getQuantity();
            }
        }
        return qty;
    }

    /**
     * free槽位数量按ID
     * @param itemId 物品ID
     * @param required required
     * @return 返回值
     */
    public int freeSlotCountById(int itemId, int required) {
        List<Item> itemList = listById(itemId);
        int openSlot = 0;

        if (!ItemConstants.isRechargeable(itemId)) {
            for (Item item : itemList) {
                required -= item.getQuantity();

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        } else {
            for (Item item : itemList) {
                required -= 1;

                if (required >= 0) {
                    openSlot++;
                    if (required == 0) {
                        return openSlot;
                    }
                } else {
                    return openSlot;
                }
            }
        }

        return -1;
    }

    /**
     * list按ID
     * @param itemId 物品ID
     * @return 返回值
     */
    public List<Item> listById(int itemId) {
        List<Item> ret = new ArrayList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * linked列表按ID
     * @param itemId 物品ID
     * @return 返回值
     */
    public List<Item> linkedListById(int itemId) {
        List<Item> ret = new LinkedList<>();
        for (Item item : list()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }

        if (ret.size() > 1) {
            ret.sort((i1, i2) -> i1.getPosition() - i2.getPosition());
        }

        return ret;
    }

    /**
     * 添加物品
     * @param item 物品
     * @return 返回值
     */
    public short addItem(Item item) {
        short slotId = addSlot(item);
        if (slotId == -1) {
            return -1;
        }
        item.setPosition(slotId);
        return slotId;
    }

    /**
     * 添加物品从DB
     * @param item 物品
     */
    public void addItemFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(InventoryType.EQUIPPED)) {
            return;
        }
        addSlotFromDB(item.getPosition(), item);
    }

    private static boolean isSameOwner(Item source, Item target) {
        return source.getOwner().equals(target.getOwner());
    }

    /**
     * 移动
     * @param sSlot sSlot
     * @param dSlot dSlot
     * @param slotMax slotMax
     */
    public void move(short sSlot, short dSlot, short slotMax) {
        lock.lock();
        try {
            Item source = inventory.get(sSlot);
            Item target = inventory.get(dSlot);
            if (source == null) {
                return;
            }
            if (target == null) {
                source.setPosition(dSlot);
                inventory.put(dSlot, source);
                inventory.remove(sSlot);
            } else if (target.getItemId() == source.getItemId() && !ItemConstants.isRechargeable(source.getItemId()) && isSameOwner(source, target)) {
                if (type.getType() == InventoryType.EQUIP.getType() || type.getType() == InventoryType.CASH.getType()) {
                    swap(target, source);
                } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                    short rest = (short) ((source.getQuantity() + target.getQuantity()) - slotMax);
                    source.setQuantity(rest);
                    target.setQuantity(slotMax);
                } else {
                    target.setQuantity((short) (source.getQuantity() + target.getQuantity()));
                    inventory.remove(sSlot);
                }
            } else {
                swap(target, source);
            }
        } finally {
            lock.unlock();
        }
    }

    private void swap(Item source, Item target) {
        inventory.remove(source.getPosition());
        inventory.remove(target.getPosition());
        short swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        inventory.put(source.getPosition(), source);
        inventory.put(target.getPosition(), target);
    }

    /**
     * 获取物品
     * @param slot 槽位
     * @return 返回值
     */
    public Item getItem(short slot) {
        lock.lock();
        try {
            return inventory.get(slot);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除物品
     * @param slot 槽位
     */
    public void removeItem(short slot) {
        removeItem(slot, (short) 1, false);
    }

    /**
     * 移除物品
     * @param slot 槽位
     * @param quantity 数量
     * @param allowZero allowZero
     */
    public void removeItem(short slot, short quantity, boolean allowZero) {
        Item item = getItem(slot);
        if (item == null) {// TODO is it ok not to throw an exception here?
            return;
        }
        item.setQuantity((short) (item.getQuantity() - quantity));
        if (item.getQuantity() < 0) {
            item.setQuantity((short) 0);
        }
        if (item.getQuantity() == 0 && !allowZero) {
            removeSlot(slot);
        }
    }

    protected short addSlot(Item item) {
        if (item == null) {
            return -1;
        }

        short slotId;
        lock.lock();
        try {
            slotId = getNextFreeSlot();
            if (slotId < 0) {
                return -1;
            }

            inventory.put(slotId, item);
        } finally {
            lock.unlock();
        }

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            // deadlocks with coupons rates found thanks to GabrielSin & Masterrulax
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }

        return slotId;
    }

    protected void addSlotFromDB(short slot, Item item) {
        lock.lock();
        try {
            inventory.put(slot, item);
        } finally {
            lock.unlock();
        }

        if (ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 移除槽位
     * @param slot 槽位
     */
    public void removeSlot(short slot) {
        Item item;
        lock.lock();
        try {
            item = inventory.remove(slot);
        } finally {
            lock.unlock();
        }

        if (item != null && ItemConstants.isRateCoupon(item.getItemId())) {
            ThreadManager.getInstance().newTask(() -> owner.updateCouponRates());
        }
    }

    /**
     * 判断是否为Full
     * @return 返回值
     */
    public boolean isFull() {
        lock.lock();
        try {
            return inventory.size() >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断是否为Full
     * @param margin margin
     * @return 返回值
     */
    public boolean isFull(int margin) {
        lock.lock();
        try {
            //System.out.print("(" + inventory.size() + " " + margin + " <> " + slotLimit + ")");
            return inventory.size() + margin >= slotLimit;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断是否为FullAfterSomeItems
     * @param margin margin
     * @param used used
     * @return 返回值
     */
    public boolean isFullAfterSomeItems(int margin, int used) {
        lock.lock();
        try {
            //System.out.print("(" + inventory.size() + " " + margin + " <> " + slotLimit + " -" + used + ")");
            return inventory.size() + margin >= slotLimit - used;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取NextFree槽位
     * @return 返回值
     */
    public short getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }

        lock.lock();
        try {
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    return i;
                }
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取NumFree槽位
     * @return 返回值
     */
    public short getNumFreeSlot() {
        if (isFull()) {
            return 0;
        }

        lock.lock();
        try {
            short free = 0;
            for (short i = 1; i <= slotLimit; i++) {
                if (!inventory.containsKey(i)) {
                    free++;
                }
            }
            return free;
        } finally {
            lock.unlock();
        }
    }

    private static boolean checkItemRestricted(List<Pair<Item, InventoryType>> items) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        // thanks Shavit for noticing set creation that would be only effective in rare situations
        for (Pair<Item, InventoryType> p : items) {
            int itemid = p.getLeft().getItemId();
            if (ii.isPickupRestricted(itemid) && p.getLeft().getQuantity() > 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查Spot
     * @param chr 角色
     * @param item 物品
     * @return 返回值
     */
    public static boolean checkSpot(Character chr, Item item) {    // thanks Vcoc for noticing pshops not checking item stacks when taking item back
    }

    /**
     * 检查Spot
     * @param chr 角色
     * @param items items
     * @return 返回值
     */
    public static boolean checkSpot(Character chr, List<Item> items) {
        List<Pair<Item, InventoryType>> listItems = new LinkedList<>();
        for (Item item : items) {
            listItems.add(new Pair<>(item, item.getInventoryType()));
        }

        return checkSpotsAndOwnership(chr, listItems);
    }

    /**
     * 检查Spots
     * @param chr 角色
     * @param items items
     * @return 返回值
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpots(chr, items, false);
    }

    /**
     * 检查Spots
     * @param chr 角色
     * @param items items
     * @param useProofInv useProofInv
     * @return 返回值
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        int invTypesSize = InventoryType.values().length;
        List<Integer> zeroedList = new ArrayList<>(invTypesSize);
        for (byte i = 0; i < invTypesSize; i++) {
            zeroedList.add(0);
        }

        return checkSpots(chr, items, zeroedList, useProofInv);
    }

    /**
     * 检查Spots
     * @param chr 角色
     * @param items items
     * @param typesSlotsUsed typesSlotsUsed
     * @param useProofInv useProofInv
     * @return 返回值
     */
    public static boolean checkSpots(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        // assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0.

        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Integer, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Integer, Byte> rcvTypes = new LinkedHashMap<>();

        for (Pair<Item, InventoryType> item : items) {
            Integer itemId = item.left.getItemId();
            List<Integer> qty = rcvItems.get(itemId);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemId, itemQtyList);
                rcvTypes.put(itemId, item.right.getType());
            } else {
                if (!ItemConstants.isEquipment(itemId) && !ItemConstants.isRechargeable(itemId)) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Integer, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                int result = InventoryManipulator.checkSpaceProgressively(c, it.getKey(), itValue, "", usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    private static long fnvHash32(final String k) {
        final int FNV_32_INIT = 0x811c9dc5;
        final int FNV_32_PRIME = 0x01000193;

        int rv = FNV_32_INIT;
        final int len = k.length();
        for (int i = 0; i < len; i++) {
            rv ^= k.charAt(i);
            rv *= FNV_32_PRIME;
        }

        return rv >= 0 ? rv : (2L * Integer.MAX_VALUE) + rv;
    }

    private static Long hashKey(Integer itemId, String owner) {
        return (itemId.longValue() << 32L) + fnvHash32(owner);
    }

    /**
     * 检查SpotsAndOwnership
     * @param chr 角色
     * @param items items
     * @return 返回值
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items) {
        return checkSpotsAndOwnership(chr, items, false);
    }

    /**
     * 检查SpotsAndOwnership
     * @param chr 角色
     * @param items items
     * @param useProofInv useProofInv
     * @return 返回值
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, boolean useProofInv) {
        List<Integer> zeroedList = new ArrayList<>(5);
        for (byte i = 0; i < 5; i++) {
            zeroedList.add(0);
        }

        return checkSpotsAndOwnership(chr, items, zeroedList, useProofInv);
    }

    /**
     * 检查SpotsAndOwnership
     * @param chr 角色
     * @param items items
     * @param typesSlotsUsed typesSlotsUsed
     * @param useProofInv useProofInv
     * @return 返回值
     */
    public static boolean checkSpotsAndOwnership(Character chr, List<Pair<Item, InventoryType>> items, List<Integer> typesSlotsUsed, boolean useProofInv) {
        //assumption: no "UNDEFINED" or "EQUIPPED" items shall be tested here, all counts are >= 0 and item list to be checked is a legal one.

        if (!checkItemRestricted(items)) {
            return false;
        }

        Map<Long, List<Integer>> rcvItems = new LinkedHashMap<>();
        Map<Long, Byte> rcvTypes = new LinkedHashMap<>();
        Map<Long, String> rcvOwners = new LinkedHashMap<>();

        for (Pair<Item, InventoryType> item : items) {
            Long itemHash = hashKey(item.left.getItemId(), item.left.getOwner());
            List<Integer> qty = rcvItems.get(itemHash);

            if (qty == null) {
                List<Integer> itemQtyList = new LinkedList<>();
                itemQtyList.add((int) item.left.getQuantity());

                rcvItems.put(itemHash, itemQtyList);
                rcvTypes.put(itemHash, item.right.getType());
                rcvOwners.put(itemHash, item.left.getOwner());
            } else {
                // thanks BHB88 for pointing out an issue with rechargeable items being stacked on inventory check
                if (!ItemConstants.isEquipment(item.left.getItemId()) && !ItemConstants.isRechargeable(item.left.getItemId())) {
                    qty.set(0, qty.get(0) + item.left.getQuantity());
                } else {
                    qty.add((int) item.left.getQuantity());
                }
            }
        }

        Client c = chr.getClient();
        for (Entry<Long, List<Integer>> it : rcvItems.entrySet()) {
            int itemType = rcvTypes.get(it.getKey()) - 1;
            int itemId = (int) (it.getKey() >> 32L);

            for (Integer itValue : it.getValue()) {
                int usedSlots = typesSlotsUsed.get(itemType);

                //System.out.print("inserting " + itemId.intValue() + " with type " + itemType + " qty " + it.getValue() + " owner '" + rcvOwners.get(it.getKey()) + "' current usedSlots:");
                //for(Integer i : typesSlotsUsed) System.out.print(" " + i);
                int result = InventoryManipulator.checkSpaceProgressively(c, itemId, itValue, rcvOwners.get(it.getKey()), usedSlots, useProofInv);
                boolean hasSpace = ((result % 2) != 0);
                //System.out.print(" -> hasSpace: " + hasSpace + " RESULT : " + result + "\n");

                if (!hasSpace) {
                    return false;
                }
                typesSlotsUsed.set(itemType, (result >> 1));
            }
        }

        return true;
    }

    /**
     * 获取类型
     * @return 返回值
     */
    public InventoryType getType() {
        return type;
    }

    /**
     * 返回迭代器
     * @return 返回值
     */
    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(list()).iterator();
    }

    /**
     * 查找按现金ID
     * @param cashId cashId
     * @return 返回值
     */
    public Item findByCashId(int cashId) {
        boolean isRing = false;
        Equip equip = null;
        for (Item item : list()) {
            if (item.getInventoryType().equals(InventoryType.EQUIP)) {
                equip = (Equip) item;
                isRing = equip.getRingId() > -1;
            }
            if ((item.getPetId() > -1 ? item.getPetId() : isRing ? equip.getRingId() : item.getCashId()) == cashId) {
                return item;
            }
        }

        return null;
    }

    /**
     * checked
     * @return 返回值
     */
    /**
     * checked
     * @return 返回值
     */
    public boolean checked() {
        lock.lock();
        try {
            return checked;
        } finally {
            lock.unlock();
        }
    }

    /**
     * checked
     * @param yes yes
     */
    /**
     * checked
     * @param yes yes
     */
    /**
     * 标记背包已检查状态
     * @param yes 是否
     */
    public void checked(boolean yes) {
        lock.lock();
        try {
            checked = yes;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加锁背包
     */
    public void lockInventory() {
        lock.lock();
    }

    /**
     * 解锁背包
     */
    public void unlockInventory() {
        lock.unlock();
    }

    /**
     * dispose
     */
    /**
     * dispose
     */
    /**
     * 销毁并清空背包内容
     */
    public void dispose() {
        owner = null;
    }
}
