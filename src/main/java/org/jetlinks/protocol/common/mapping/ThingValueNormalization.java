package org.jetlinks.protocol.common.mapping;

/**
 * 物模型取值标准化，埋点：报文转物模型时
 * 例如：类型转换，特定值转空值
 *
 * @author v-lizy81
 * @date 2024/6/29 23:02
 */
public interface ThingValueNormalization<T> {

    public T apply(Object itemValue);

}
