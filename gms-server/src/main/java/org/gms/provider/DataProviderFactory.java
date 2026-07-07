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
*/
package org.gms.provider;

import org.gms.provider.wz.WZFiles;
import org.gms.provider.wz.XMLWZFile;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * WZ 数据提供者工厂，根据 {@link WZFiles} 枚举创建带语言回退的 {@link DataProvider}。
 * <p>
 * 若存在 {@code wz-语言/} 目录，则优先从语言目录读取已本地化的 XML；
 * 缺失文件自动回退到默认 {@code wz/} 目录，无需复制完整 WZ 包。
 * </p>
 */
public class DataProviderFactory {
    /** 从指定路径创建 XML WZ 数据提供者。 */
    private static DataProvider getWZ(Path in) {
        return new XMLWZFile(in);
    }

    /**
     * 获取指定 WZ 包的数据提供者（含语言目录回退）。
     *
     * @param in WZ 包枚举（如 {@link WZFiles#ITEM}）
     * @return 可直接调用 {@link DataProvider#getData(String)} 的提供者实例
     */
    public static DataProvider getDataProvider(WZFiles in) {
        Path basePath = in.getBaseFile();
        Path languagePath = in.getLanguageFile();
        DataProvider baseProvider = getWZ(basePath);
        if (!Files.exists(languagePath) || languagePath.equals(basePath)) {
            return baseProvider;
        }
        // 中文 WZ 只维护被本地化过的文件，缺失的文件继续回退到原始 WZ。
        return new LocalizedDataProvider(getWZ(languagePath), baseProvider);
    }
}
