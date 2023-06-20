package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

public class SumAndModCRCCalculator implements CRCCalculator {

    private int beginIdx;

    private int endIdx;

    private int mod;

    public SumAndModCRCCalculator(int beginIdx, int endIdx, int mod) {
        this.beginIdx = beginIdx;
        this.endIdx = endIdx;
        this.mod = mod;
    }

    @Override
    public int apply(ByteBuf buf) {
        byte[] wholeBuf = buf.array();

        int sum = 0;
        int eIdx = (endIdx > 0 ? endIdx : wholeBuf.length + endIdx);
        for (int i = beginIdx; i < eIdx; i++) {
            sum += wholeBuf[i];
        }

        return sum % mod;
    }

}
