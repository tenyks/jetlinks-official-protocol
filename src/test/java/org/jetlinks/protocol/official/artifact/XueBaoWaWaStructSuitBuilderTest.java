package org.jetlinks.protocol.official.artifact;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.official.binary2.*;
import org.junit.Test;

public class XueBaoWaWaStructSuitBuilderTest {

    private StructSuit structSuit = XueBaoWaWaStructSuitBuilder.buildV26();

    @Test
    public void buildStartGameStructDcl() {
        StructInstance structInst = structSuit.createStructInstance("CMD:0x31");

        structInst.addFieldInstance("messageId", (short)100);
        structInst.addFieldInstance("timeOut", (byte)1);
        structInst.addFieldInstance("result", (short)0);
        structInst.addFieldInstance("pickUpCF", (short)2);
        structInst.addFieldInstance("toTopCF", (short)3);
        structInst.addFieldInstance("moveCF", (short)4);
        structInst.addFieldInstance("bigCF", (short)5);
        structInst.addFieldInstance("pickupHeight", (short)6);
        structInst.addFieldInstance("letDownLength", (short)0);
        structInst.addFieldInstance("fbMotorSpeed", (short)3);
        structInst.addFieldInstance("lrMotorSpeed", (short)2);
        structInst.addFieldInstance("udMotorSpeed", (short)1);

        ByteBuf rst = structSuit.serialize(structInst);
        byte[] buf = new byte[rst.writerIndex()];
        rst.readBytes(buf);
        System.out.println(Hex.encodeHex(buf));
    }



}