package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * 字节流中特征码提取器
 *
 * @author v-lizy81
 * @date 2023/6/12 23:59
 */
public interface BinaryFeatureCodeExtractor {

    String extract(ByteBuf buf);

    /**
     * 检查字节流是否二次HEX编码
     * @param buf   待检查的字节流，（非空）
     * @return  如果是二次HEX编码返回true
     */
    default boolean isDoubleHex(ByteBuf buf) {
        return false;
    }

    boolean isValidFeatureCode(String featureCode);

}
