package org.gms.client.keybind;

import org.gms.net.packet.OutPacket;

import java.util.Arrays;

/**
 * 快捷栏按键绑定数据模型，存储 8 个快捷栏槽位与按键的映射关系。
 */
public class QuickslotBinding {
    public static final int QUICKSLOT_SIZE = 8;
    public static final byte[] DEFAULT_QUICKSLOTS = {0x2A, 0x52, 0x47, 0x49, 0x1D, 0x53, 0x4F, 0x51};

    private final byte[] m_aQuickslotKeyMapped;

    // Initializes quickslot object for the user.
    // aKeys' length has to be 8.
    /**
     * 构造快捷栏绑定，键值数组长度必须为 8。
     *
     * @param aKeys 8 个快捷栏按键映射值
     */
    public QuickslotBinding(byte[] aKeys) {
        if (aKeys.length != QUICKSLOT_SIZE) {
            throw new IllegalArgumentException(String.format("aKeys' size should be %d", QUICKSLOT_SIZE));
        }

        this.m_aQuickslotKeyMapped = aKeys.clone();
    }

    /**
     * 将快捷栏绑定编码写入输出封包。
     *
     * @param p 输出封包
     */
    public void encode(OutPacket p) {
        // Quickslots are default.
        // The client will skip them and call CQuickslotKeyMappedMan::DefaultQuickslotKeyMap.
        if (Arrays.equals(this.m_aQuickslotKeyMapped, DEFAULT_QUICKSLOTS)) {
            p.writeBool(false);
            return;
        }

        p.writeBool(true);

        for (byte nKey : this.m_aQuickslotKeyMapped) {
            // For some reason Nexon sends these as integers, similar to CFuncKeyMappedMan.
            // However there's no evidence any key can be above 0xFF anyhow.
            // Regardless, we need to encode an integer to avoid an error 38 crash; as CFuncKeyMapped::m_aQuickslotKeyMapped is int[8].
            p.writeInt(nKey);
        }
    }

    /**
     * 获取快捷栏按键映射数组。
     *
     * @return 8 个槽位的按键映射副本
     */
    public byte[] GetKeybindings() {
        return m_aQuickslotKeyMapped;
    }

}