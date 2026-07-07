package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 四元组容器，用于将四个值绑定在一起传递。
 *
 * @param <A> 第一个元素的类型
 * @param <B> 第二个元素的类型
 * @param <C> 第三个元素的类型
 * @param <D> 第四个元素的类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quartet<A, B, C, D> {
    private A first;
    private B second;
    private C third;
    private D fourth;
}
