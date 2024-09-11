package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.tenyks.core.crc.SumAndModCRCCalculator;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class SumAndModCRCCalculatorTest {

    private SumAndModCRCCalculator calculator = new SumAndModCRCCalculator(6, -1, 100);

    @Test
    public void apply() throws DecoderException {
        String input = "FE 00 09 01 FF F6 14 31 00 00 00 00 00 00 00 00 00 00 00 45".replace(" ", "");
        ByteBuf case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
        System.out.println(Integer.toHexString(calculator.apply(case1)));

        input = "fe 00 08 01 ff f7 14 31 78 00 03 02 03 04 01 0f 01 02 03 17".replace(" ", "");
        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
        System.out.println(Integer.toHexString(calculator.apply(case1)));

//        input = "fe 00 02 01 ff fd 14 31 3c 00 00 00 00 00 09 21 00 00 00 47".replace(" ", "");
//        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
//        System.out.println(Integer.toHexString(calculator.apply(case1)));
//
//        input = "fe 00 00 01 ff ff 14 31 3c 00 00 00 00 00 00 00 00 00 00 1D".replace(" ", "");
//        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
//        System.out.println(Integer.toHexString(calculator.apply(case1)));
//
//        input = "FE 00 0A 01 FF F5 14 31 B4 00 00 00 00 00 09 15 00 00 00 17".replace(" ", "");
//        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
//        System.out.println(Integer.toHexString(calculator.apply(case1)));
//
//        input = "fe 00 07 01 ff f8 0c 32 00 01 f4 33".replace(" ", "");
//        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
//        System.out.println(Integer.toHexString(calculator.apply(case1)));

        input = "fe000301fffc0c3200f40133";
        case1 = Unpooled.wrappedBuffer(Hex.decodeHex(input));
        System.out.println(Integer.toHexString(calculator.apply(case1)));
    }
}