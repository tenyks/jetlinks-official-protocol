package org.jetlinks.protocol.official.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于位表示的字典本
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/21
 * @since V3.1.0
 */
public class BitDictBook<T> {

    private final Map<T, Item<T>> index = new HashMap<>();

    private short   maxOffset = 0;

    public List<Item<T>> decode(byte[] bytes) {
        return index.values().stream().filter(item -> item.maskTrue(bytes)).collect(Collectors.toList());
    }

    public byte[] encode(List<T> codes) {
        byte[] rst = new byte[maxOffset + 1];

        for (T code : codes) {
            Item<T> item = index.get(code);
            if (item == null) continue;

            item.compositeWith(rst);
        }

        return rst;
    }

    public BitDictBook<T> add(T code, short offset, byte mask, byte val, String description) {
        if (code == null) {
            throw new IllegalArgumentException("参数不全。[0x08DB2061]");
        }

        index.put(code, new Item<>(code, offset, mask, description));
        maxOffset = (short)Math.max(maxOffset, offset);

        return this;
    }

    public BitDictBook<T> add(T code, short offset, byte mask, String description) {
        if (code == null) {
            throw new IllegalArgumentException("参数不全。[0x08DB2061]");
        }

        index.put(code, new Item<>(code, offset, mask, description));
        maxOffset = (short)Math.max(maxOffset, offset);

        return this;
    }

    public static class Item<T> {
        /**
         * 唯一编码
         */
        private final T         code;


        private final short     offset;

        /**
         * 表示掩码
         */
        private final byte         mask;

        /**
         * 描述
         */
        private final String        description;

        public Item(T code, short offset, byte mask, String description) {
            this.code = code;
            this.offset = offset;
            this.mask = mask;
            this.description = description;
        }

        public boolean maskTrue(byte[] bytes) {
            if (bytes.length <= offset) return false;

            return (bytes[offset] & mask) != 0;
        }

        public byte[] compositeWith(byte[] bytes) {
            if (bytes.length <= offset) return bytes;

            bytes[offset] |= mask;

            return bytes;
        }

        public T getCode() {
            return code;
        }

        public short getOffset() {
            return offset;
        }

        public byte getMask() {
            return mask;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "code=" + code +
                    ", offset=" + offset +
                    ", mask=" + Integer.toBinaryString(0x000000ff & (int)mask) +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}
