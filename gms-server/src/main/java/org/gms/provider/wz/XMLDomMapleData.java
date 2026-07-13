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

import org.gms.constants.game.GameConstants;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.gms.provider.Data;
import org.gms.provider.DataEntity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 基于XML DOM的WZ数据实现类，实现Data接口。
 * 使用W3C DOM解析XML格式的WZ数据文件，提供对WZ数据节点的访问能力。
 * 支持各种数据类型（整数、浮点、字符串、向量、画布等）的解析和类型转换。
 *
 * @author OdinMS Team
 */
public class XMLDomMapleData implements Data {
    /** XML节点引用 */
    private final Node node;
    /** 图片数据目录路径，用于解析CANVAS类型的图片资源 */
    private Path imageDataDir;

    /**
     * 从文件输入流构造XML DOM数据对象
     * @param fis XML文件输入流
     * @param imageDataDir 图片数据目录
     */
    public XMLDomMapleData(FileInputStream fis, Path imageDataDir) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(fis);
            this.node = document.getFirstChild();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.imageDataDir = imageDataDir;
    }

    /**
     * 私有构造函数，从现有XML节点创建数据对象（内部使用）
     * @param node XML节点
     */
    private XMLDomMapleData(Node node) {
        this.node = node;
    }

    /**
     * 根据路径获取子节点，支持使用"/"分隔的多级路径和".."返回上级目录
     * 注意：该方法使用synchronized保证线程安全，在高并发读取场景下可能成为瓶颈
     * @param path 子节点路径，如"info/icon"或"../parent"
     * @return 找到的子节点，如果不存在则返回null
     */
    @Override
    public synchronized Data getChildByPath(String path) {  // the whole XML reading system seems susceptible to give nulls on strenuous read scenarios
        String[] segments = path.split("/");
        if (segments[0].equals("..")) {
            return ((Data) getParent()).getChildByPath(path.substring(path.indexOf("/") + 1));
        }

        Node myNode;
        myNode = node;
        for (String s : segments) {
            NodeList childNodes = myNode.getChildNodes();
            boolean foundChild = false;
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() == Node.ELEMENT_NODE
                        && childNode.getAttributes().getNamedItem("name").getNodeValue().equals(s)) {
                    myNode = childNode;
                    foundChild = true;
                    break;
                }
            }
            if (!foundChild) {
                return null;
            }
        }

        XMLDomMapleData ret = new XMLDomMapleData(myNode);
        ret.imageDataDir = imageDataDir.resolve(getName().trim()).resolve(path).getParent();
        return ret;
    }

    /**
     * 获取当前节点的所有直接子节点（仅包含ELEMENT_NODE类型节点）
     * @return 子数据节点列表
     */
    @Override
    public synchronized List<Data> getChildren() {
        List<Data> ret = new ArrayList<>();

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                XMLDomMapleData child = new XMLDomMapleData(childNode);
                child.imageDataDir = imageDataDir.resolve(getName().trim());
                ret.add(child);
            }
        }

        return ret;
    }

    /**
     * 获取当前节点存储的数据值，根据节点类型进行解析和类型转换
     * 支持的类型：DOUBLE、FLOAT、INT、SHORT、STRING、UOL、VECTOR、CANVAS
     * @return 解析后的数据对象（类型由DataType决定）
     */
    @Override
    public synchronized Object getData() {
        NamedNodeMap attributes = node.getAttributes();
        DataType type = getType();
        switch (type) {
            case DOUBLE:
            case FLOAT:
            case INT:
            case SHORT: {
                String value = attributes.getNamedItem("value").getNodeValue();
                Number nval = GameConstants.parseNumber(value);

                switch (type) {
                    case DOUBLE:
                        return nval.doubleValue();
                    case FLOAT:
                        return nval.floatValue();
                    case INT:
                        return nval.intValue();
                    case SHORT:
                        return nval.shortValue();
                    default:
                        return null;
                }
            }
            case STRING:
            case UOL: {
                String value = attributes.getNamedItem("value").getNodeValue();
                return value;
            }
            case VECTOR: {
                String x = attributes.getNamedItem("x").getNodeValue();
                String y = attributes.getNamedItem("y").getNodeValue();
                return new Point(Integer.parseInt(x), Integer.parseInt(y));
            }
            case CANVAS: {
                String width = attributes.getNamedItem("width").getNodeValue();
                String height = attributes.getNamedItem("height").getNodeValue();
                return new Point(Integer.parseInt(width), Integer.parseInt(height));
            }
            default:
                return null;
        }
    }

    /**
     * 获取当前节点的数据类型，根据XML节点名称映射到DataType枚举
     * 节点名称与类型的对应关系：
     * imgdir -> PROPERTY, canvas -> CANVAS, convex -> CONVEX, sound -> SOUND,
     * uol -> UOL, double -> DOUBLE, float -> FLOAT, int -> INT, short -> SHORT,
     * string -> STRING, vector -> VECTOR, null -> IMG_0x00
     * @return 数据类型
     */
    @Override
    public synchronized DataType getType() {
        String nodeName = node.getNodeName();

        switch (nodeName) {
            case "imgdir":
                return DataType.PROPERTY;
            case "canvas":
                return DataType.CANVAS;
            case "convex":
                return DataType.CONVEX;
            case "sound":
                return DataType.SOUND;
            case "uol":
                return DataType.UOL;
            case "double":
                return DataType.DOUBLE;
            case "float":
                return DataType.FLOAT;
            case "int":
                return DataType.INT;
            case "short":
                return DataType.SHORT;
            case "string":
                return DataType.STRING;
            case "vector":
                return DataType.VECTOR;
            case "null":
                return DataType.IMG_0x00;
        }
        return null;
    }

    /**
     * 获取父节点，如果当前节点是文档根节点则返回null
     * @return 父数据实体
     */
    @Override
    public synchronized DataEntity getParent() {
        Node parentNode;
        parentNode = node.getParentNode();
        if (parentNode.getNodeType() == Node.DOCUMENT_NODE) {
            return null;
        }
        XMLDomMapleData parentData = new XMLDomMapleData(parentNode);
        parentData.imageDataDir = imageDataDir.getParent();
        return parentData;
    }

    /**
     * 获取节点名称（从XML节点的name属性获取）
     * @return 节点名称
     */
    @Override
    public synchronized String getName() {
        return node.getAttributes().getNamedItem("name").getNodeValue();
    }

    /**
     * 获取子节点迭代器，用于for-each遍历
     * @return 子数据节点的迭代器
     */
    @Override
    public synchronized Iterator<Data> iterator() {
        return getChildren().iterator();
    }

    /**
     * 获取指定名称的XML属性值
     * @param name 属性名称
     * @return 属性值，如果属性不存在则返回null
     */
    public synchronized String getAttributeValue(String name) {
        Node attr = node.getAttributes().getNamedItem(name);
        return attr == null ? null : attr.getNodeValue();
    }
}
