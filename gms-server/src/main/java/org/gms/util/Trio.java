package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三元组容器，用于将三个值绑定在一起传递。
 *
 * @param <A> 第一个元素的类型
 * @param <B> 第二个元素的类型
 * @param <C> 第三个元素的类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trio<A, B, C> {
    private A first;
    private B second;
    private C third;
}
