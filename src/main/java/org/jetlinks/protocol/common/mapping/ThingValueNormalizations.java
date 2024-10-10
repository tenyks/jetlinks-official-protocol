package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.BaseDataTypeConvertors;
import org.jetlinks.protocol.official.common.DictBook;

import java.util.function.Function;

/**
 * @author v-lizy81
 * @date 2024/6/29 23:11
 */
public class ThingValueNormalizations {

    /**
     * 归一化：输出Integer并且特定值转换为null
     */
    public static ThingValueNormalization<Integer> ofToIntAndSpecAsNull(final Integer defVal, final Object specNull) {
        return new ThingValueNormalization<Integer>() {

            private final Function<Object, Integer> toIntFun = BaseDataTypeConvertors.ofToInt(defVal);

            @Override
            public Integer apply(Object itemValue) {
                if (itemValue == null) return defVal;
                if (itemValue.equals(specNull)) return null;

                return toIntFun.apply(itemValue);
            }
        };
    }

    public static ThingValueNormalization<Integer> ofToInt(final Integer defVal) {
        return new ThingValueNormalization<Integer>() {

            private final Function<Object, Integer> toIntFun = BaseDataTypeConvertors.ofToInt(defVal);

            @Override
            public Integer apply(Object itemValue) {
                if (itemValue == null) return defVal;

                return toIntFun.apply(itemValue);
            }
        };
    }

    public static ThingValueNormalization<Integer> plusOffsetAndToInt(final int offset) {
        return new ThingValueNormalization<Integer>() {

            private final Function<Object, Integer> toIntFun = BaseDataTypeConvertors.ofToInt(0);

            @Override
            public Integer apply(Object itemValue) {
                if (itemValue == null) return offset;

                return toIntFun.apply(itemValue) + offset;
            }
        };
    }

    public static ThingValueNormalization<Float> ofToFloat(final Float defVal) {
        return new ThingValueNormalization<Float>() {

            private final Function<Object, Float> toIntFun = BaseDataTypeConvertors.ofToFloat(defVal);

            @Override
            public Float apply(Object itemValue) {
                if (itemValue == null) return defVal;

                return toIntFun.apply(itemValue);
            }
        };
    }

    public static <R> ThingValueNormalization<R> ofToDictVal(final DictBook<?, R> dictBook, final R defVal) {
        return itemValue -> {
            if (itemValue == null) return defVal;

            DictBook.Item<?, R> item = dictBook.getOrCreate(itemValue);
            if (item == null) return defVal;

            return item.getOutputCode();
        };
    }

}
