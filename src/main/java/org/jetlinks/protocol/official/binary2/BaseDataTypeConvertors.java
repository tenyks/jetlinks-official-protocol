package org.jetlinks.protocol.official.binary2;

import java.util.function.Function;

/**
 * 基础数据类型转换
 *
 * @author v-lizy81
 * @date 2024/6/29 22:36
 */
public class BaseDataTypeConvertors {

    public static Function<Object, Integer> ofToInt(final Integer defVal) {
        return o -> {
            if (o instanceof Number) {
                return ((Number) o).intValue();
            } else if (o instanceof String) {
                //TODO 补充字符串转数值
            }

            return defVal;
        };
    }

    public static Function<Object, Short> ofToShort(final Short defVal) {
        return o -> {
            if (o instanceof Number) {
                return ((Number) o).shortValue();
            } else if (o instanceof String) {
                //TODO 补充字符串转数值
            }

            return defVal;
        };
    }

    public static Function<Object, Long> ofToLong(final Long defVal) {
        return o -> {
            if (o instanceof Number) {
                return ((Number) o).longValue();
            } else if (o instanceof String) {
                //TODO 补充字符串转数值
            }

            return defVal;
        };
    }

    public static Function<Object, Float> ofToFloat(final Float defVal) {
        return o -> {
            if (o instanceof Number) {
                return ((Number) o).floatValue();
            } else if (o instanceof String) {
                //TODO 补充字符串转数值
            }

            return defVal;
        };
    }

}
