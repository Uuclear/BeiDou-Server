package org.gms.net.server.coordinator.session;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IP 地址工具类，判断本机回环地址与局域网私有地址段。
 */
public class IpAddresses {
    private static final List<Pattern> LOCAL_ADDRESS_PATTERNS = loadLocalAddressPatterns();

    private static List<Pattern> loadLocalAddressPatterns() {
        return Stream.of("^10\\.", "^192\\.168\\.", "^172\\.(1[6-9]|2[0-9]|3[0-1])\\.")
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    /**
     * 判断是否为本地回环地址（127.x.x.x）。
     *
     * @param inetAddress IP 地址字符串
     * @return 是回环地址返回 true
     */
    public static boolean isLocalAddress(String inetAddress) {
        return inetAddress.startsWith("127.");
    }

    /**
     * 判断是否为局域网私有地址（10.x、192.168.x、172.16-31.x）。
     *
     * @param inetAddress IP 地址字符串
     * @return 是局域网地址返回 true
     */
    public static boolean isLanAddress(String inetAddress) {
        return LOCAL_ADDRESS_PATTERNS.stream()
                .anyMatch(pattern -> matchesPattern(pattern, inetAddress));
    }

    private static boolean matchesPattern(Pattern pattern, String searchTerm) {
        return pattern.matcher(searchTerm).find();
    }
}
