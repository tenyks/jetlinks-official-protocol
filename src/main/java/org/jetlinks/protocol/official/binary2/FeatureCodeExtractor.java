package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * 字节流中特征码提取器
 *
 * @author v-lizy81
 * @date 2023/6/12 23:59
 */
public interface FeatureCodeExtractor {

    String extract(ByteBuf buf);

}
