package me.tenyks.qiyun.protocol;

import io.netty.buffer.ByteBuf;
import me.tenyks.core.crc.CRCCalculator;
import me.tenyks.core.crc.XORCRCCalculator;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.common.mapping.ThingItemMapping;
import org.jetlinks.protocol.common.mapping.ThingItemMappings;
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

    private static final short      DATA_BEGIN_IDX = 6;

    private static final int        MAX_TIME = 30000;

    private static final short      MAX_MONEY = 30000;

    private static final String     CODE_OF_CMD_FIELD = "CMD";

    private static final String     CODE_OF_MSG_TYPE_FIELD = "MSG_ID";

    private static final String     CODE_OF_MSG_NO_FIELD = "MSG_NO";

    private static final String     CODE_OF_ENCY_FLAG_FIELD = "ENCY_NO";

    /**
     * 充电桩登录认证消息[上行], 0x01
     */
    private static DefaultStructDeclaration buildAuthRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩登录认证消息[上行]", "CMD:0x01");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("AuthRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 30));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x01));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfPileType());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = new DefaultFieldDeclaration("充电枪数量", "gunCount", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("通信协议版本", "protocolVersion", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("程序版本", "firmwareVersion", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("网络链接类型", "networkType", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("SIM卡", "simNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("运营商", "simNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 登录认证应答[下行], 0x02
     */
    private static DefaultStructDeclaration buildAuthResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("登录认证应答[下行]", "CMD:0x02");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("AuthResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 30));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x01));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfPileType());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = new DefaultFieldDeclaration("充电枪数量", "gunCount", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("通信协议版本", "protocolVersion", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("程序版本", "firmwareVersion", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("网络链接类型", "networkType", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("SIM卡", "simNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        fieldDcl = new DefaultFieldDeclaration("运营商", "simNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo() {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.CHARS7, DATA_BEGIN_IDX);
    }

    /**
     * 数据字段：桩类型
     */
    private static DefaultFieldDeclaration buildDFDclOfPileType() {
        return new DefaultFieldDeclaration("桩类型", "pileType", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 1));
    }

    /**
     * 公共字段：帧头
     */
    private static DefaultFieldDeclaration buildSOP() {
        return new DefaultFieldDeclaration("公共：帧头", "SOP", BaseDataType.UINT8, (short)0)
                .setDefaultValue((byte)0x68);
    }

    /**
     * 公共字段：报文长度
     * @param sizeOfData DATA字段的长度，单位：字节
     */
    private static DefaultFieldDeclaration buildLENFieldDcl(byte sizeOfData) {
        return new DefaultFieldDeclaration("报文长度", "LEN", BaseDataType.UINT8, (short) 1)
                .setDefaultValue(sizeOfData + 8);
    }

    /**
     * 公共字段：报文序号
     */
    private static DefaultFieldDeclaration buildMsgNoFieldDcl() {
        return new DefaultFieldDeclaration("报文序号", CODE_OF_MSG_NO_FIELD, BaseDataType.UINT16, (short) 2)
                .setDefaultValue(1);
    }

    /**
     * 公共字段：加密标志
     */
    private static DefaultFieldDeclaration buildEncyFlagFieldDcl() {
        return new DefaultFieldDeclaration("加密标志", CODE_OF_ENCY_FLAG_FIELD, BaseDataType.UINT8, (short) 4)
                .setDefaultValue(1);
    }

    /**
     * 公共字段：报文类型
     */
    private static DefaultFieldDeclaration buildMsgTypeFieldDcl(byte msgType) {
        return new DefaultFieldDeclaration("报文类型", CODE_OF_MSG_TYPE_FIELD, BaseDataType.UINT8, (short) 5)
                .setDefaultValue(msgType);
    }

    /**
     * 公共字段：校验
     */
    private static DefaultFieldDeclaration buildCRCFieldDcl() {
        return new DefaultFieldDeclaration("校验", "CHECK_SUM", BaseDataType.UINT16, (short) -1)
                    .setDefaultValue((short) 0x0000);
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

    private static class YKCV1FeatureCodeExtractor implements FeatureCodeExtractor {
        public static final byte MAGIC_ID_OF_V1 = (byte) 0x68;

        @Override
        public String extract(ByteBuf buf) {
            byte[] headerBuf = new byte[6];

            buf.readerIndex(0);
            if (buf.readableBytes() < headerBuf.length) {
                return "[YKCV1]WRONG_SIZE:" + Hex.encodeHexString(buf.array());
            }
            buf.readBytes(headerBuf);

            byte frameType = headerBuf[5];
            if (headerBuf[0] != MAGIC_ID_OF_V1) {
                return "[YKCV1]WRONG_MAGIC_ID:" + Hex.encodeHexString(headerBuf);
            }

            return "CMD:0x" + String.format("%02X", 0x00ff & frameType);
        }

        @Override
        public boolean isValidFeatureCode(String featureCode) {
            return (featureCode != null && featureCode.startsWith("CMD:"));
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
