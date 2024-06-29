package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.BaseDataTypeConvertors;

import java.util.function.Function;

/**
 * @author v-lizy81
 * @date 2024/6/29 23:11
 */
public class ThingValueNormalizations {

    public static ThingValueNormalization<Integer> ofToIntAndMaskSpecNull(final Integer defVal, final Object maskNull) {
        return new ThingValueNormalization<Integer>() {

            private final Function<Object, Integer> toIntFun = BaseDataTypeConvertors.ofToInt(defVal);

            @Override
            public Integer apply(Object itemValue) {
                if (itemValue == null) return defVal;
                if (itemValue.equals(maskNull)) return null;

                return toIntFun.apply(itemValue);
            }
        };
    }

}
