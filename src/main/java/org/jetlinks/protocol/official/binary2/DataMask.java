package org.jetlinks.protocol.official.binary2;

/**
 * 数值掩码
 *
 * @author v-lizy81
 * @date 2024/8/21 23:18
 */
public interface DataMask {

    Object mask(Object srcVal);

    static DataMask create(byte mask) {
        return null;
    }
}
