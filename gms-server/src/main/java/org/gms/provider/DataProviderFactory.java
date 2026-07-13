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
 * 数据提供者工厂类，负责创建DataProvider实例。
 * 支持创建基于XML格式的WZ数据提供者，并自动处理本地化语言包的回退逻辑。
 *
 * @author OdinMS Team
 */
public class DataProviderFactory {
    /**
     * 创建指定路径的WZ数据提供者（XML格式）
     * @param in WZ文件目录路径
     * @return 数据提供者实例
     */
    private static DataProvider getWZ(Path in) {
        return new XMLWZFile(in);
    }

    /**
     * 根据WZFiles枚举获取数据提供者，自动处理本地化。
     * 如果存在对应语言的本地化WZ目录，则优先使用；否则使用基础WZ目录。
     * 本地化WZ中缺失的文件会自动回退到基础WZ。
     *
     * @param in WZ文件枚举类型
     * @return 配置好的数据提供者实例
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
