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

import org.gms.provider.wz.DataType;

import java.awt.*;

/**
 * 数据工具类，提供从Data节点中安全获取各种类型数据的静态方法。
 * 支持String、int、short、long、float、double、Point等类型的获取，
 * 包含类型转换、默认值处理、路径访问等便捷功能，是解析WZ数据的常用工具。
 *
 * @author OdinMS Team
 */
public class DataTool {
    /**
     * 从数据节点获取String类型值
     * @param data 数据节点
     * @return 字符串值
     */
    public static String getString(Data data) {
        return ((String) data.getData());
    }

    /**
     * 从数据节点获取String类型值，支持默认值
     * @param data 数据节点
     * @param def 默认值（当data为null或数据为null时返回）
     * @return 字符串值或默认值
     */
    public static String getString(Data data, String def) {
        if (data == null || data.getData() == null) {
            return def;
        } else {
            return ((String) data.getData());
        }
    }

    /**
     * 通过路径从数据节点获取String类型值
     * @param path 子节点路径
     * @param data 父数据节点
     * @return 字符串值
     */
    public static String getString(String path, Data data) {
        return getString(data.getChildByPath(path));
    }

    /**
     * 通过路径从数据节点获取String类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return 字符串值或默认值
     */
    public static String getString(String path, Data data, String def) {
        return getString(data.getChildByPath(path), def);
    }

    /**
     * 从数据节点获取double类型值
     * @param data 数据节点
     * @return double值
     */
    public static double getDouble(Data data) {
        return (Double) data.getData();
    }

    /**
     * 从数据节点获取float类型值
     * @param data 数据节点
     * @return float值
     */
    public static float getFloat(Data data) {
        return (Float) data.getData();
    }

    /**
     * 从数据节点获取int类型值，null时返回0
     * @param data 数据节点
     * @return int值
     */
    public static int getInt(Data data) {
        if (data == null || data.getData() == null) {
            return 0;// DEF?
        }
        return (Integer) data.getData();
    }

    /**
     * 通过路径从数据节点获取int类型值
     * @param path 子节点路径
     * @param data 父数据节点
     * @return int值
     */
    public static int getInt(String path, Data data) {
        return getInt(data.getChildByPath(path));
    }

    /**
     * 从数据节点获取int类型值，自动转换STRING类型
     * @param data 数据节点
     * @return int值
     */
    public static int getIntConvert(Data data) {
        if (data.getType() == DataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            return getInt(data);
        }
    }

    /**
     * 从数据节点获取int类型值，自动转换STRING类型并支持默认值，
     * 支持处理带百分号的字符串（如"50%"）
     * @param data 数据节点
     * @param def 默认值
     * @return int值或默认值
     */
    public static int getIntConvert(Data data, int def) {
        if (data == null) {
            return def;
        }
        if (data.getType() == DataType.STRING) {
            String dd = getString(data);
            if (dd.endsWith("%")) {
                dd = dd.substring(0, dd.length() - 1);
            }
            try {
                return Integer.parseInt(dd);
            } catch (NumberFormatException nfe) {
                return def;
            }
        } else {
            return getInt(data, def);
        }
    }

    /**
     * 通过路径从数据节点获取int类型值，自动转换STRING类型
     * @param path 子节点路径
     * @param data 父数据节点
     * @return int值
     */
    public static int getIntConvert(String path, Data data) {
        Data d = data.getChildByPath(path);
        if (d.getType() == DataType.STRING) {
            return Integer.parseInt(getString(d));
        } else {
            return getInt(d);
        }
    }

    /**
     * 从数据节点获取int类型值，支持默认值和STRING/Short类型自动转换
     * @param data 数据节点
     * @param def 默认值
     * @return int值或默认值
     */
    public static int getInt(Data data, int def) {
        if (data == null || data.getData() == null) {
            return def;
        } else if (data.getType() == DataType.STRING) {
            return Integer.parseInt(getString(data));
        } else {
            Object numData = data.getData();
            if (numData instanceof Integer) {
                return (Integer) numData;
            } else {
                return (Short) numData;
            }
        }
    }

    /**
     * 通过路径从数据节点获取int类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return int值或默认值
     */
    public static int getInt(String path, Data data, int def) {
        if (data==null) {
            return def;
        }
        return getInt(data.getChildByPath(path), def);
    }

    /**
     * 通过路径从数据节点获取int类型值，自动转换STRING类型并支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return int值或默认值
     */
    public static int getIntConvert(String path, Data data, int def) {
        Data d = data.getChildByPath(path);
        if (d == null) {
            return def;
        }
        if (d.getType() == DataType.STRING) {
            try {
                return Integer.parseInt(getString(d));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                return def;
            }
        } else {
            return getInt(d, def);
        }
    }

    /**
     * 通过路径从数据节点获取Integer包装类型值，自动处理各种Number类型
     * @param path 子节点路径
     * @param data 父数据节点
     * @return Integer值，不存在时返回null
     */
    public static Integer getInteger(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Integer.parseInt(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).intValue();
        }
    }

    /**
     * 通过路径从数据节点获取int类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return int值或默认值
     */
    public static int getInteger(String path, Data data, int def) {
        Integer val = getInteger(path, data);
        return val == null ? def : val;
    }

    /**
     * 通过路径从数据节点获取Short包装类型值
     * @param path 子节点路径
     * @param data 父数据节点
     * @return Short值，不存在时返回null
     */
    public static Short getShort(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Short.parseShort(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).shortValue();
        }
    }

    /**
     * 通过路径从数据节点获取short类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return short值或默认值
     */
    public static short getShort(String path, Data data, short def) {
        Short val = getShort(path, data);
        return val == null ? def : val;
    }

    /**
     * 通过路径从数据节点获取Long包装类型值
     * @param path 子节点路径
     * @param data 父数据节点
     * @return Long值，不存在时返回null
     */
    public static Long getLong(String path, Data data) {
        Data child = data.getChildByPath(path);
        if (child == null || child.getData() == null) {
            return null;
        } else if (child.getType() == DataType.STRING) {
            return Long.parseLong(getString(child));
        } else {
            Object numData = child.getData();
            return ((Number) numData).longValue();
        }
    }

    /**
     * 通过路径从数据节点获取long类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return long值或默认值
     */
    public static long getLong(String path, Data data, long def) {
        Long val = getLong(path, data);
        return val == null ? def : val;
    }

    /**
     * 从数据节点获取Point类型值（坐标点）
     * @param data 数据节点
     * @return Point对象
     */
    public static Point getPoint(Data data) {
        return ((Point) data.getData());
    }

    /**
     * 通过路径从数据节点获取Point类型值
     * @param path 子节点路径
     * @param data 父数据节点
     * @return Point对象
     */
    public static Point getPoint(String path, Data data) {
        return getPoint(data.getChildByPath(path));
    }

    /**
     * 通过路径从数据节点获取Point类型值，支持默认值
     * @param path 子节点路径
     * @param data 父数据节点
     * @param def 默认值
     * @return Point对象或默认值
     */
    public static Point getPoint(String path, Data data, Point def) {
        final Data pointData = data.getChildByPath(path);
        if (pointData == null) {
            return def;
        }
        return getPoint(pointData);
    }

    /**
     * 获取数据节点的完整路径（从根节点到当前节点）
     * @param data 数据节点
     * @return 完整路径字符串
     */
    public static String getFullDataPath(Data data) {
        String path = "";
        DataEntity myData = data;
        while (myData != null) {
            path = myData.getName() + "/" + path;
            myData = myData.getParent();
        }
        return path.substring(0, path.length() - 1);
    }

    /**
     * 获取数据节点的指定XML属性值
     * @param data 数据节点
     * @param name 属性名称
     * @return 属性值
     */
    public static String getAttributeValue(Data data,String name) {
        return data.getAttributeValue(name);
    }

    /**
     * 获取数据节点的指定XML属性值，支持默认值
     * @param data 数据节点
     * @param name 属性名称
     * @param def 默认值
     * @return 属性值或默认值
     */
    public static String getAttributeValue(Data data,String name,String def) {
        String val = getAttributeValue(data,name);
        return val == null ? def : val;
    }

    /**
     * 获取数据节点的指定XML属性值并转换为int类型，支持默认值
     * @param data 数据节点
     * @param name 属性名称
     * @param def 默认值
     * @return 属性的int值或默认值
     */
    public static int getAttributeValueInt(Data data,String name,int def) {
        String val = getAttributeValue(data,name);
        return val == null ? def : Integer.parseInt(val);
    }
}
