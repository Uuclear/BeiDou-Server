package org.gms.provider;

/**
 * 带语言回退的 WZ 数据提供者装饰器。
 * <p>
 * 读取数据时优先使用本地化 {@link DataProvider}，文件缺失时回退到原始 WZ。
 * 目录导航（{@link #getRoot()}）始终使用原始 WZ 的完整文件树，确保未翻译资源仍可枚举。
 * </p>
 */
public class LocalizedDataProvider implements DataProvider {
    private final DataProvider localized;
    private final DataProvider fallback;

    /**
     * @param localized 语言目录对应的 WZ 提供者
     * @param fallback  默认 WZ 提供者，用于回退
     */
    public LocalizedDataProvider(DataProvider localized, DataProvider fallback) {
        this.localized = localized;
        this.fallback = fallback;
    }

    @Override
    public Data getData(String path) {
        Data data = localized.getData(path);
        // 语言目录里没有该 XML 时，使用原始 WZ，避免为了少量翻译复制整包资源。
        return data != null ? data : fallback.getData(path);
    }

    @Override
    public DataDirectoryEntry getRoot() {
        // 导航目录保持原始 WZ 的完整文件树，确保未翻译资源仍然可枚举。
        return fallback.getRoot();
    }
}
