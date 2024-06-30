package org.jetlinks.protocol.official.common;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 字典本
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/30
 * @since V3.1.0
 */
public class DictBook<T, R> {

    private final Map<T, Item<T, R>> index = new HashMap<>();

    private Function<T, R>  otherMapFun;

    private String          otherDescription;

    /**
     * 优先
     */
    private Function<T, Item<T, R>> otherBuildFun;

    public DictBook<T, R> add(T srcCode, R dstCode, String description) {
        if (srcCode == null || dstCode == null) {
            throw new IllegalArgumentException("参数不全。[0x08DB2061]");
        }

        index.put(srcCode, new Item<>(srcCode, dstCode, description));

        return this;
    }

    public DictBook<T, R> addOtherItemTemplate(Function<T, R> mapFun, String description) {
        this.otherMapFun = mapFun;
        this.otherDescription = (description != null ? description : "其他：");

        return this;
    }

    public DictBook<T, R> addOtherItemBuilder(Function<T, Item<T, R>> buildFun) {
        this.otherBuildFun = buildFun;

        return this;
    }

    @Nullable
    public Item<T, R> getOrCreate(Object srcCode) {
        Item<T, R> item = index.get(srcCode);
        if (item != null) return item;

        if (otherBuildFun != null) {
            item = otherBuildFun.apply((T)srcCode);
            index.put(item.srcCode, item);

            return item;
        }

        if (otherMapFun != null) {
            item = new Item<>((T) srcCode, otherMapFun.apply((T) srcCode), otherDescription + "(" + srcCode.toString() + ")");
            index.put(item.srcCode, item);
        }

        return null;
    }

    public static class Item<T, R> {
        private final T srcCode;

        private final R outputCode;

        private final String description;

        public Item(T srcCode, R outputCode, String description) {
            this.srcCode = srcCode;
            this.outputCode = outputCode;
            this.description = description;
        }

        public T getSrcCode() {
            return srcCode;
        }

        public R getOutputCode() {
            return outputCode;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "DictBook.Item{" +
                    "srcCode=" + srcCode +
                    ", outputCode=" + outputCode +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}
