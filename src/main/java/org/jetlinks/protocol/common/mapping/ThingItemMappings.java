package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.common.DictBook;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/30
 * @since V3.1.0
 */
public class ThingItemMappings {

    /**
     * 标志字典翻译为xxxCode和xxxDesc字段
     */
    public static ThingItemMapping<String> ofDictExtend(final DictBook<Short, String> dictBook, final String itemDescKey) {
        return new ThingItemMapping<String>() {
            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                DictBook.Item<Short, String> item = dictBook.getOrCreate(itemVal);

                if (item != null) {
                    return Arrays.asList(
                            Tuples.of(itemKey, item.getOutputCode()),
                            Tuples.of(itemDescKey, item.getDescription())
                    );
                } else {
                    return Arrays.asList(
                            Tuples.of(itemKey, itemVal.toString()),
                            Tuples.of(itemDescKey, "其他：" + itemVal.toString())
                    );
                }
            }

        };
    }

    public static ThingItemMapping<String> ofDictExtendPostfix(final DictBook<Short, String> dictBook, final String itemDescKeyPostfix) {
        return new ThingItemMapping<String>() {
            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                DictBook.Item<Short, String> item = dictBook.getOrCreate(itemVal);

                String itemDescKey = itemKey + itemDescKeyPostfix;
                if (item != null) {
                    return Arrays.asList(
                            Tuples.of(itemKey, item.getOutputCode()),
                            Tuples.of(itemDescKey, item.getDescription())
                    );
                } else {
                    return Arrays.asList(
                            Tuples.of(itemKey, itemVal.toString()),
                            Tuples.of(itemDescKey, "其他：" + itemVal.toString())
                    );
                }
            }

        };
    }

}
