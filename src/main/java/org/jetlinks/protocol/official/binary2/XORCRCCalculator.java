package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * 异或运算求校验值
 *
 * @author v-lizy81
 * @date 2024/6/26 23:25
 */
public class XORCRCCalculator implements CRCCalculator {

    public XORCRCCalculator(int beginIdx, int endIdx) {

    }

    @Override
    public int apply(ByteBuf buf) {
        return 0;
    }

}
