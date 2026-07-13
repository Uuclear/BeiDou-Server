package org.gms.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三元组类
 * <p>
 * 用于存储三个相关的值，使用Lombok注解自动生成getter、setter、
 * 构造函数、equals、hashCode和toString方法。
 * </p>
 *
 * @param <A> 第一个值的类型
 * @param <B> 第二个值的类型
 * @param <C> 第三个值的类型
 * @author GMS Team
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trio<A, B, C> {

    /**
     * 第一个值
     */
    private A first;

    /**
     * 第二个值
     */
    private B second;

    /**
     * 第三个值
     */
    private C third;
}
