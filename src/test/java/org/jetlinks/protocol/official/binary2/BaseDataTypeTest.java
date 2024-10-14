package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hswebframework.utils.DateTimeUtils;
import org.jetlinks.core.utils.BytesUtils;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class BaseDataTypeTest {

    @Test
    public void testFloatType() {
        float val = 0.0f;
        ByteBuf buf = Unpooled.buffer(4);

        BaseDataType.FLOAT.write(buf, val);
        System.out.println(ByteUtils.toHexStr(buf));

        Object rVal = BaseDataType.FLOAT.read(buf, (short) 4);
        System.out.println(rVal);
        Assert.assertEquals(val, rVal);

        buf.resetReaderIndex().resetWriterIndex();
        val = 1.0f;
        BaseDataType.FLOAT.write(buf, val);
        System.out.println(ByteUtils.toHexStr(buf));

        rVal = BaseDataType.FLOAT.read(buf, (short) 4);
        System.out.println(rVal);
        Assert.assertEquals(val, rVal);

        buf.resetReaderIndex().resetWriterIndex();
        val = -1.1f;
        BaseDataType.FLOAT.write(buf, val);
        System.out.println(ByteUtils.toHexStr(buf));
        rVal = BaseDataType.FLOAT.read(buf, (short) 4);
        System.out.println(rVal);
        Assert.assertEquals(val, rVal);
    }

    @Test
    public void testFloatType2() throws DecoderException {
        // 参考http://www.styb.cn/cms/ieee_754.php
        String payload = "3F A3 D7 0A".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(payload));

        Object val;
        val = BaseDataType.FLOAT.read(input, (short)4);
        System.out.println(val);
        Assert.assertEquals(1.28f, val);
    }

    @Test
    public void testReadCP56Time2a() throws DecoderException {
        String payload = "A7232E0FB80217";
        ByteBuf input = BytesUtils.fromHexStrWithTrim(payload);

        Date rst;
        rst = (Date)BaseDataType.CP56Time2a.read(input, (short)7);
        System.out.println(rst);

        String actual = DateTimeUtils.format(rst, "yyyy-MM-dd HH:mm:ss");
        Assert.assertEquals("2023-02-24 15:46:09", actual);
    }

    @Test
    public void testWriteCP56Time2a() {
        Date input = DateTimeUtils.formatDateString("2023-02-24 15:46:09.127", "yyyy-MM-dd HH:mm:ss.SSS");

        ByteBuf buf = Unpooled.buffer(7);
        BaseDataType.CP56Time2a.write(buf, input);

        String actual = ByteUtils.toHexStr(buf);
        System.out.println(actual);

        String expected = "A7232E0FB80217";
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInt32LE() throws DecoderException {
        String payload = "D0 FB 01 00".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(payload));

        Object val;
        val = BaseDataType.INT32LE.read(input, (short)4);
        System.out.println(val);
        Assert.assertEquals(130000, val);

        ByteBuf output = Unpooled.buffer(100);
        BaseDataType.INT32LE.write(output, 130000);
        String outputStr = ByteUtils.toHexStr(output);
        System.out.println(outputStr);
        Assert.assertEquals("D0FB0100", outputStr);
    }

    @Test
    public void testInt40LE() throws DecoderException {
        String payload = "D0 FB 01 00 00".replace(" ", "");
        ByteBuf input = Unpooled.wrappedBuffer(Hex.decodeHex(payload));

        Object val;
        val = BaseDataType.INT40LE.read(input, (short)4);
        System.out.println(val);
        Assert.assertEquals(130000L, val);

        ByteBuf output = Unpooled.buffer(100);
        BaseDataType.INT40LE.write(output, 130000);
        String outputStr = ByteUtils.toHexStr(output);
        System.out.println(outputStr);
        Assert.assertEquals("D0FB010000", outputStr);
    }
}