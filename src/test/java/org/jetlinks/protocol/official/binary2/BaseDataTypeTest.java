package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.official.core.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

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
}