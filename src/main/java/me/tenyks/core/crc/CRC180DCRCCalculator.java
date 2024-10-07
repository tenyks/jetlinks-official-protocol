package me.tenyks.core.crc;

import io.netty.buffer.ByteBuf;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/7
 * @since V3.1.0
 */
public class CRC180DCRCCalculator implements CRCCalculator {

    private final int   beginIdx;

    private final int   endIdx;

    private final CRC180DChecksum   checksum;

    public CRC180DCRCCalculator(int beginIdx, int endIdx) {
        this.beginIdx = beginIdx;
        this.endIdx = endIdx;
        this.checksum = new CRC180DChecksum();
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public int apply(ByteBuf buf) {
        byte[]  wholeBuf = buf.array();

        int eIdx = (endIdx > 0 ? endIdx : buf.writerIndex() + endIdx);
        int len = eIdx - beginIdx;

        checksum.update(wholeBuf, beginIdx, len);

        return (int)checksum.getValue();
    }

}
