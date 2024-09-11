package me.tenyks.core.crc;

import io.netty.buffer.ByteBuf;

public class SumAndModCRCCalculator implements CRCCalculator {

    private final int beginIdx;

    private final int endIdx;

    private final int mod;

    /**
     *
     * @param beginIdx      开始索引，从0开始
     * @param endIdx        结束索引，（不包含结束），大于0时标识数组位置，小于等0时表示array.length - endIdx位置
     * @param mod
     */
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
