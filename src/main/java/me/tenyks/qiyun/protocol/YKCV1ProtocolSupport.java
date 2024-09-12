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
     * 充电桩心跳包[上行], 0x03
     * <li>10秒周期上送，用于链路状态判断，3次未收到心跳包视为网络异常，需要重新登陆</li>
     */
    private static DefaultStructDeclaration buildHeartBeatPingStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩心跳包[上行]", "CMD:0x03");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeartBeatPing"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x03));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo());

        DefaultFieldDeclaration fieldDcl;

        //0x00：正常 0x01：故障
        fieldDcl = buildDataFieldDcl("枪状态", "gunStatus", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 心跳包应答[下行], 0x04
     */
    private static DefaultStructDeclaration buildHeartBeatPongStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("心跳包应答[下行]", "CMD:0x04");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("HeartBeatPong"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x04));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("心跳应答", "pongFlag", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 8));
        structDcl.addField(fieldDcl.setDefaultValue((byte) 0));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 计费模型验证请求[上行], 0x05
     * <li>主动请求，直到成功</li>
     * <li>充电桩在登陆成功后，都需要对当前计费模型校验，如计费模型与平台当前不一致，则需要向平台请求新的计费模型</li>
     */
    private static DefaultStructDeclaration buildCheckFeeTermsRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型验证请求[上行]", "CMD:0x05");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CheckFeeTermsRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 9));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x03));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo());

        DefaultFieldDeclaration fieldDcl;

        //首次连接到平台时置零
        fieldDcl = buildDataFieldDcl("计费模型编号", "termNo", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 计费模型验证请求应答[下行], 0x06
     */
    private static DefaultStructDeclaration buildCheckFeeTermsResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型验证请求应答[下行]", "CMD:0x06");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CheckFeeTermsResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 10));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x06));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termNo", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(fieldDcl);

        //0x00 桩计费模型与平台一致 0x01 桩计费模型与平台不一致
        fieldDcl = buildDataFieldDcl("验证结果", "checkRstFlag", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 9));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电桩计费模型请求[上行], 0x09
     * <li>主动请求，直到成功</li>
     * <li>充电桩计费模型与平台不一致时，都需要请求计费模型，如计费模型请求不成功，则禁止充电</li>
     */
    private static DefaultStructDeclaration buildFeeTermsRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电桩计费模型请求[上行]", "CMD:0x09");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FeeTermsRequest"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x03));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 计费模型请求应答[下行], 0x0A
     * <li>用户充电费用计算，每半小时为一个费率段，共 48 段，每段对应尖峰平谷其中一个费率，充电时桩屏幕按此费率分别显示已充电费和服务费</li>
     */
    private static DefaultStructDeclaration buildFeeTermsResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("计费模型请求应答[下行]", "CMD:0x0A");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("FeeTermsResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 90));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x0A));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termNo", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(fieldDcl);

        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖费电费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 9));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖服务费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 13));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰电费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 17));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰服务费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 21));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平电费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 25));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平服务费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 29));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷电费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 33));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷服务费费率", "checkRstFlag", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 37));
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("计损比例", "checkRstFlag", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 41));
        structDcl.addField(fieldDcl);

        for (int i = 0; i < 24; i++) {
            //0x00：尖费率 0x01：峰费率 0x02：平费率 0x03：谷费率
            fieldDcl = buildDataFieldDcl(String.format("%02d:00～%02d:30时段费率号", i, i), "checkRstFlag", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 42 + i));
            structDcl.addField(fieldDcl);
            fieldDcl = buildDataFieldDcl(String.format("%02d:30～%02d:00时段费率号", i, i + 1), "checkRstFlag", BaseDataType.UINT8, (short) (DATA_BEGIN_IDX + 43 + i));
            structDcl.addField(fieldDcl);
        }

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 读取实时监测数据[下行], 0x12
     * <li>主动请求</li>
     * <li>运营平台根据需要主动发起读取实时数据的请求</li>
     */
    private static DefaultStructDeclaration buildCallOfMonitorDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("读取实时监测数据[下行]", "CMD:0x12");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CallOfMonitorData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x12));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo());

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 上传实时监测数据[上行], 0x13
     * <li>周期上送、变位上送、召唤</li>
     * <li>上送充电枪实时数据，周期上送时，待机 5 分钟、充电 15 秒</li>
     */
    private static DefaultStructDeclaration buildReportMonitorDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上传实时监测数据[下行]", "CMD:0x13");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CheckFeeTermsResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 60));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x13));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("交易流水号", "transNo", BaseDataType.CHARS16, (short)(DATA_BEGIN_IDX + 7));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildDFDclOfPileNo((short)(DATA_BEGIN_IDX + 16)));
        structDcl.addField(buildDFDclOfGunNo());

        //0x00：离线 0x01：故障 0x02：空闲 0x03：充电 需做到变位上送
        fieldDcl = buildDataFieldDcl("状态", "status", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 23));
        structDcl.addField(fieldDcl);

        //0x00 否 0x01 是 0x02 未知 （无法检测到枪是否插回枪座即 未知）
        fieldDcl = buildDataFieldDcl("枪是否归位", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 9));
        structDcl.addField(fieldDcl);

        //0x00 否 0x01 是需做到变位上送
        fieldDcl = buildDataFieldDcl("是否插枪", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 10));
        structDcl.addField(fieldDcl);

        //精确到小数点后一位；待机置零
        fieldDcl = buildDataFieldDcl("输出电压", "gunRel", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 11));
        structDcl.addField(fieldDcl);
        //精确到小数点后一位；待机置零
        fieldDcl = buildDataFieldDcl("输出电流", "gunRel", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 13));
        structDcl.addField(fieldDcl);


        //0x00 否 0x01 是 0x02 未知 （无法检测到枪是否插回枪座即 未知）
        fieldDcl = buildDataFieldDcl("枪是否归位", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 9));
        structDcl.addField(fieldDcl);
        //0x00 否 0x01 是 0x02 未知 （无法检测到枪是否插回枪座即 未知）
        fieldDcl = buildDataFieldDcl("枪是否归位", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 9));
        structDcl.addField(fieldDcl);
        //0x00 否 0x01 是 0x02 未知 （无法检测到枪是否插回枪座即 未知）
        fieldDcl = buildDataFieldDcl("枪是否归位", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 9));
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
    }/**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo(short absOffset) {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.CHARS7, absOffset);
    }

    /**
     * 数据字段：桩类型
     */
    private static DefaultFieldDeclaration buildDFDclOfPileType() {
        return new DefaultFieldDeclaration("桩类型", "pileType", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 7));
    }

    /**
     * 数据字段：枪号
     */
    private static DefaultFieldDeclaration buildDFDclOfGunNo() {
        return new DefaultFieldDeclaration("枪号", "gunNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 7));
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
