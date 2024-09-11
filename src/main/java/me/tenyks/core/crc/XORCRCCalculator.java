package me.tenyks.core.crc;

import io.netty.buffer.ByteBuf;

/**
 * 异或运算求校验值
 *
 * @author v-lizy81
 * @date 2024/6/26 23:25
 */
public class XORCRCCalculator implements CRCCalculator {

    private final int   beginIdx;

    private final int   endIdx;

    /**
     *
     * @param beginIdx
     * @param endIdx    0和正整数表示从左往右计数，负整数标识从右往左计数
     */
    public XORCRCCalculator(int beginIdx, int endIdx) {
        this.beginIdx = beginIdx;
        this.endIdx = endIdx;
    }

    @Override
    public int apply(ByteBuf buf) {
        byte[]  wholeBuf = buf.array();

        byte crc = 0;
        int eIdx = (endIdx > 0 ? endIdx : wholeBuf.length + endIdx);
        for (int i = beginIdx; i < eIdx; i++) {
            crc ^= wholeBuf[i];
        }

        return crc;
    }

}
