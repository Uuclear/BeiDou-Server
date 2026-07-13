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
package org.gms.provider.wz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataDirectoryEntry;
import org.gms.provider.DataProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 基于XML文件的WZ数据提供者实现，实现DataProvider接口。
 * 从文件系统中读取XML格式的WZ数据文件，解析为XMLDomMapleData对象。
 * 在初始化时会递归扫描目录结构，构建用于导航的目录树。
 *
 * @author OdinMS Team
 */
public class XMLWZFile implements DataProvider {
	private static final Logger log = LoggerFactory.getLogger(DataProvider.class);
	/** WZ根目录路径 */
	private final Path root;
    /** 用于导航的根目录条目 */
    private final WZDirectoryEntry rootForNavigation;

    /**
     * 构造XML WZ文件数据提供者
     * @param fileIn WZ根目录路径
     */
    public XMLWZFile(Path fileIn) {
        root = fileIn;
        rootForNavigation = new WZDirectoryEntry(fileIn.getFileName().toString(), 0, 0, null);
        fillMapleDataEntitys(root, rootForNavigation);
    }

    /**
     * 递归填充WZ数据实体，扫描目录结构并构建目录/文件条目树
     * 目录：不以.img结尾的子目录
     * 文件：以.xml结尾的文件（去掉.xml后缀作为文件名）
     * @param lroot 当前扫描的本地目录路径
     * @param wzdir 对应的WZ目录条目
     */
    private void fillMapleDataEntitys(Path lroot, WZDirectoryEntry wzdir) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lroot)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                if (Files.isDirectory(path) && !fileName.endsWith(".img")) {
                    WZDirectoryEntry newDir = new WZDirectoryEntry(fileName, 0, 0, wzdir);
                    wzdir.addDirectory(newDir);
                    fillMapleDataEntitys(path, newDir);
                } else if (fileName.endsWith(".xml")) {
                    wzdir.addFile(new WZFileEntry(fileName.substring(0, fileName.length() - 4), 0, 0, wzdir));
                }
            }
        } catch (IOException e) {
            log.warn("Can not open file/directory at " + lroot.toAbsolutePath().toString());
        }
    }

    /**
     * 根据路径获取数据，从对应的.xml文件加载并解析为XML DOM数据
     * @param path 数据路径（不含.xml后缀）
     * @return 解析后的数据节点，如果文件不存在则返回null
     * @throws RuntimeException 当文件不存在或IO错误时抛出
     */
    @Override
    public synchronized Data getData(String path) {
        Path dataFile = root.resolve(path + ".xml");
        Path imageDataDir = root.resolve(path);
        if (!Files.exists(dataFile)) {
            return null;
        }
        final XMLDomMapleData domMapleData;
        try (FileInputStream fis = new FileInputStream(dataFile.toString())) {
            domMapleData = new XMLDomMapleData(fis, imageDataDir.getParent());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Datafile " + path + " does not exist in " + root.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return domMapleData;
    }

    /**
     * 获取根目录条目，用于遍历整个WZ文件结构
     * @return 根目录条目
     */
	@Override
	public DataDirectoryEntry getRoot() {
		return rootForNavigation;
	}
}
