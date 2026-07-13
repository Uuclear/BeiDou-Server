package org.gms.provider;

/**
 * 本地化数据提供者，实现了DataProvider接口，用于支持多语言资源。
 * 采用回退机制：优先从本地化（翻译）数据提供者获取数据，
 * 如果本地化数据不存在，则回退到基础数据提供者，避免重复复制整包资源。
 *
 * @author GMS Server Team
 */
public class LocalizedDataProvider implements DataProvider {
    /** 本地化数据提供者（如中文WZ） */
    private final DataProvider localized;
    /** 回退数据提供者（原始基础WZ） */
    private final DataProvider fallback;

    /**
     * 构造本地化数据提供者
     * @param localized 本地化数据提供者
     * @param fallback 回退数据提供者（基础数据）
     */
    public LocalizedDataProvider(DataProvider localized, DataProvider fallback) {
        this.localized = localized;
        this.fallback = fallback;
    }

    /**
     * 获取数据，优先从本地化提供者获取，不存在则回退到基础提供者
     * @param path 数据路径
     * @return 数据节点
     */
    @Override
    public Data getData(String path) {
        Data data = localized.getData(path);
        // 语言目录里没有该 XML 时，使用原始 WZ，避免为了少量翻译复制整包资源。
        return data != null ? data : fallback.getData(path);
    }

    /**
     * 获取根目录条目，使用基础WZ的完整文件树以确保所有资源可枚举
     * @return 根目录条目
     */
    @Override
    public DataDirectoryEntry getRoot() {
        // 导航目录保持原始 WZ 的完整文件树，确保未翻译资源仍然可枚举。
        return fallback.getRoot();
    }
}
