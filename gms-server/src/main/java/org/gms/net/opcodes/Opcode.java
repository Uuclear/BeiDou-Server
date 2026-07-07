package org.gms.net.opcodes;

/**
 * 操作码通用接口，定义 v83 协议中收发包操作码的基本契约。
 * <p>
 * {@link RecvOpcode} 表示客户端发往服务端的操作码；
 * {@link SendOpcode} 表示服务端发往客户端的操作码。
 * 操作码值对应封包载荷前 2 字节（小端序 short）。
 * </p>
 */
public interface Opcode {
    /** @return 操作码数值（包头中的 opcode 字段） */
    int getValue();
    /** @return 操作码枚举名称 */
    String getName();
}
