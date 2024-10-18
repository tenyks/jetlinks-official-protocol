package org.jetlinks.protocol.common.mapping;

import org.apache.commons.collections.CollectionUtils;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.BitDictBook;
import org.jetlinks.protocol.official.common.DictBook;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
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
    public static ThingItemMapping<String> ofDictExtend(DictBook<?, String> dictBook, final String itemDescKey) {
        return new AbstractThingItemMapping<String>(dictBook) {

            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                DictBook.Item<?, String> item = getItem(itemVal);

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

    public static ThingItemMapping<String> ofBitDictExtend(BitDictBook<String> dictBook, final String itemDescKey) {
        return new AbstractThingItemMappingBit<String>(dictBook) {

            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                List<BitDictBook.Item<String>> items = getItem(itemVal);

                if (CollectionUtils.isNotEmpty(items)) {
                    List<String> codeList = new ArrayList<>();
                    List<String> descList = new ArrayList<>();

                    for (BitDictBook.Item<String> item : items) {
                        codeList.add(item.getCode());
                        descList.add(item.getDescription());
                    }

                    return Arrays.asList(
                            Tuples.of(itemKey, String.join(",", codeList)),
                            Tuples.of(itemDescKey, String.join(",", descList))
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

    public static ThingItemMapping<String> ofBitDictExtend2(BitDictBook<String> dictBook, final String itemCodeKey, final String itemDescKey) {
        return new AbstractThingItemMappingBit<String>(dictBook) {

            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                List<BitDictBook.Item<String>> items = getItem(itemVal);

                if (CollectionUtils.isNotEmpty(items)) {
                    List<String> codeList = new ArrayList<>();
                    List<String> descList = new ArrayList<>();

                    for (BitDictBook.Item<String> item : items) {
                        codeList.add(item.getCode());
                        descList.add(item.getDescription());
                    }

                    return Arrays.asList(
                            Tuples.of(itemCodeKey, String.join(",", codeList)),
                            Tuples.of(itemDescKey, String.join(",", descList))
                    );
                } else {
                    return Arrays.asList(
                            Tuples.of(itemCodeKey, itemVal.toString()),
                            Tuples.of(itemDescKey, "其他：" + itemVal.toString())
                    );
                }
            }
        };
    }

    public static ThingItemMapping<String> ofBitDictExtend2(DictBook<?, String> dictBook, final String itemCodeKey, final String itemDescKey) {
        return new AbstractThingItemMapping<String>(dictBook) {

            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                DictBook.Item<?, String> item = getItem(itemVal);

                if (item != null) {
                    return Arrays.asList(
                            Tuples.of(itemCodeKey, item.getOutputCode()),
                            Tuples.of(itemDescKey, item.getDescription())
                    );
                } else {
                    return Arrays.asList(
                            Tuples.of(itemCodeKey, itemVal.toString()),
                            Tuples.of(itemDescKey, "其他：" + itemVal.toString())
                    );
                }
            }
        };
    }

    public static ThingItemMapping<String> ofDictExtendPostfix(DictBook<?, String> dictBook, final String itemDescKeyPostfix) {
        return new AbstractThingItemMapping<String>(dictBook) {
            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {
                DictBook.Item<?, String> item = getItem(itemVal);

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

    /**
     * 联合标志字段翻译，例如：总的结果FLAG + 细分的结果FLAG
     */
    public static ThingItemMapping<String>
    ofWithPreconditionDictExtend(StructFieldDeclaration preconditionFieldDcl,
                                 DictBook<?, String> preconditionDictBook,
                                 DictBook<?, String> dictBook, final String itemDescKey) {
        return new AbstractWithPreconditionThingItemMapping<String>(preconditionFieldDcl, preconditionDictBook, dictBook) {
            @Override
            public List<Tuple2<String, String>> apply(String itemKey, Object itemVal) {

                DictBook.Item<?, String> preconditionItem = getPreconditionItem();
                if (preconditionItem == null) {
                    return Arrays.asList(
                            Tuples.of(itemKey, "UNK_NULL"),
                            Tuples.of(itemDescKey, "其他：前提条件字段的取值为空")
                    );
                }
                if (!preconditionItem.isAsPassed()) {
                    return Arrays.asList(
                            Tuples.of(itemKey, preconditionItem.getOutputCode()),
                            Tuples.of(itemDescKey, preconditionItem.getDescription())
                    );
                }

                DictBook.Item<?, String> item = getItem(itemVal);
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
