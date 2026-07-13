package org.gms.net.opcodes;

/**
 * 数据包操作码接口
 * 定义获取操作码值和名称的通用方法
 * 接收操作码（RecvOpcode）和发送操作码（SendOpcode）都实现此接口
 *
 * @author OdinMS开发团队
 */
public interface Opcode {
    /**
     * 获取操作码的数值
     *
     * @return 操作码整数值
     */
    int getValue();

    /**
     * 获取操作码的名称
     *
     * @return 操作码名称字符串
     */
    String getName();
}
