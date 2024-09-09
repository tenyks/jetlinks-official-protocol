package me.tenyks.qiyun.protocol;

import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.official.binary2.*;

/**
 * 云快充新能源汽车充电桩协议
 *
 * 参考：《充电桩与云快充服务平台交互协议》，版本V1.6
 *
 * @author v-lizy81
 * @date 2024/9/9 22:20
 */
public class YKCV1ProtocolSupport {

    public static final String      NAME_AND_VER = "MI_CHONG_IEVC_V1.0.4";

    private static final short      DATA_BEGIN_IDX = 3;

    private static final int        MAX_TIME = 30000;

    private static final short      MAX_MONEY = 30000;

    private static final String     CODE_OF_CMD_FIELD = "CMD";

    /**
     * 公共字段：校验
     */
    private static DefaultFieldDeclaration buildSUMFieldDcl() {
        return new DefaultFieldDeclaration("异或校验", "SUM", BaseDataType.UINT8, (short)-1)
                .setDefaultValue((byte) 0x00);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor nextToThisAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(nextToThisAnchor, (short)0);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType, short absOffset) {
        return new DefaultFieldDeclaration(name, code, dataType, absOffset);
    }

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType,
                                                             DynamicAnchor anchor, short offsetToAnchor) {
        return new DefaultFieldDeclaration(name, code, dataType).setAnchorReference(anchor, offsetToAnchor);
    }

    private static CRCCalculator buildCRCCalculator() {
        return new XORCRCCalculator(1, -1);
    }

    private static class MiChongFeatureCodeExtractor implements FeatureCodeExtractor {
        private static final byte MAGIC_ID_OF_V2_1 = (byte) 0xAA;
        private static final byte[] MAGIC_ID_OF_V2_1_DOUBLE_HEX = new byte[]{(byte) 0x41, (byte) 0x41};

        private static final byte MAGIC_ID_OF_V2_2 = (byte) 0x55;
        private static final byte[] MAGIC_ID_OF_V2_2_DOUBLE_HEX = new byte[]{(byte) 0x35, (byte) 0x35};

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[6];

            buf.readerIndex(0);
            if (buf.readableBytes() < headerBuf.length) {
                return "[MiChong]WRONG_SIZE:" + Hex.encodeHexString(buf.array());
            }
            buf.readBytes(headerBuf);

            if (headerBuf[0] != MAGIC_ID_OF_V2_1 && headerBuf[0] != MAGIC_ID_OF_V2_2) {
                return "[MiChong]WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + String.format("%02X", 0x000000ff & headerBuf[2]);
        }

        @Override
        public boolean isValidFeatureCode(String featureCode) {
            return (featureCode != null && featureCode.startsWith("CMD:"));
        }

        @Override
        public boolean isDoubleHex(ByteBuf buf) {
            byte[] headerBuf = new byte[4];
            if (buf.readableBytes() < headerBuf.length) return false;

            buf.readBytes(headerBuf);

            return (headerBuf[0] == MAGIC_ID_OF_V2_1_DOUBLE_HEX[0] && headerBuf[1] == MAGIC_ID_OF_V2_1_DOUBLE_HEX[1])
                    ||
                    (headerBuf[0] == MAGIC_ID_OF_V2_2_DOUBLE_HEX[0] && headerBuf[1] == MAGIC_ID_OF_V2_2_DOUBLE_HEX[1]);
        }
    }

    private static class YKCV1EncodeSigner implements EncodeSigner {

        private final CRCCalculator crcCalculator = buildCRCCalculator();

        @Override
        public ByteBuf apply(ByteBuf buf) {
            int saveReaderIdx = buf.readerIndex();
            int saveWriterIdx = buf.writerIndex();

            int crc = crcCalculator.apply(buf);

            buf.writerIndex(saveWriterIdx - 1);
            buf.writeByte((byte)crc);

            buf.writerIndex(saveWriterIdx);
            buf.readerIndex(saveReaderIdx);

            return buf;
        }

    }

}
