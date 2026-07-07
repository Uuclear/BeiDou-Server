package org.gms.net.server.coordinator.session;

import java.util.regex.Pattern;

/**
 * 硬件标识（HWID）值对象，支持从主机字符串解析与格式校验。
 *
 * @param hwid 8 位十六进制 HWID 字符串
 */
public record Hwid(String hwid) {
    private static final int HWID_LENGTH = 8;
    // First part is a mac address (without dashes), second part is the hwid
    private static final Pattern VALID_HOST_STRING_PATTERN = Pattern.compile("[0-9A-F]{12}_[0-9A-F]{8}");

    private static boolean isValidHostString(String hostString) {
        return VALID_HOST_STRING_PATTERN.matcher(hostString).matches();
    }

    /**
     * 从主机字符串（MAC_HWID 格式）解析 HWID。
     *
     * @param hostString 主机标识字符串
     * @return 解析后的 HWID
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
