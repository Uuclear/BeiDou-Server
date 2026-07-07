/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.util;

/**
 * 二元组容器，用于将两个异构或同构值绑定在一起传递。
 * <p>
 * 常用于统计更新、键值对等需要成对返回的场景。
 *
 * @param <E> 左侧元素的类型
 * @param <F> 右侧元素的类型
 * @author Frz
 * @version 1.0
 * @since Revision 333
 */
public class Pair<E, F> {

    public E left;
    public F right;

    /**
     * 构造一个二元组。
     *
     * @param left  左侧元素
     * @param right 右侧元素
     */
    public Pair(E left, F right) {
        this.left = left;
        this.right = right;
    }

    /**
     * 获取左侧元素。
     *
     * @return 左侧元素
     */
    public E getLeft() {
        return left;
    }

    /**
     * 获取右侧元素。
     *
     * @return 右侧元素
     */
    public F getRight() {
        return right;
    }

    /**
     * 将二元组格式化为字符串，左右值以冒号分隔。
     *
     * @return 形如 {@code left:right} 的字符串
     */
    @Override
    public String toString() {
        return left.toString() + ":" + right.toString();
    }

    /**
     * 计算二元组的哈希值。
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }

    /**
     * 判断两个二元组是否相等（左右元素均相同）。
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair other = (Pair) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (right == null) {
            return other.right == null;
        } else return right.equals(other.right);
    }
}