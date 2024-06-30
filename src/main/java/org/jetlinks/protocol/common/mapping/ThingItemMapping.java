package org.jetlinks.protocol.common.mapping;

import reactor.util.function.Tuple2;

import java.util.List;

/**
 * 物模型属性/输入输出参数映射，例如：字典翻译
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/30
 * @since V3.1.0
 */
public interface ThingItemMapping<T> {

    List<Tuple2<String, T>> apply(String itemKey, Object itemVal);

}
