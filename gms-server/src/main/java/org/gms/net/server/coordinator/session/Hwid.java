package org.gms.net.server.coordinator.session;

import java.util.regex.Pattern;

/**
 * 硬件ID（HWID）记录类
 * 用于标识客户端硬件身份，防止多开和账号滥用
 * HWID格式为：MAC地址(12位十六进制)_硬件标识(8位十六进制)
 *
 * @author OdinMS开发团队
 */
public record Hwid(String hwid) {
    private static final int HWID_LENGTH = 8;
    // First part is a mac address (without dashes), second part is the hwid
    private static final Pattern VALID_HOST_STRING_PATTERN = Pattern.compile("[0-9A-F]{12}_[0-9A-F]{8}");

    /**
     * 验证主机字符串格式是否有效
     *
     * @param hostString 主机字符串（格式：MAC_HWID）
     * @return 格式有效返回true
     */
    private static boolean isValidHostString(String hostString) {
        return VALID_HOST_STRING_PATTERN.matcher(hostString).matches();
    }

    /**
     * 从主机字符串创建Hwid对象
     *
     * @param hostString 主机字符串（格式：MAC地址_HWID）
     * @return Hwid对象
     * @throws IllegalArgumentException 格式无效时抛出
     */
    public static Hwid fromHostString(String hostString) throws IllegalArgumentException {
        if (hostString == null || !isValidHostString(hostString)) {
            throw new IllegalArgumentException("hostString has invalid format");
        }

        final String[] split = hostString.split("_");
        if (split.length != 2 || split[1].length() != HWID_LENGTH) {
            throw new IllegalArgumentException("Hwid validation failed for hwid: " + hostString);
        }

        return new Hwid(split[1]);
    }
}
