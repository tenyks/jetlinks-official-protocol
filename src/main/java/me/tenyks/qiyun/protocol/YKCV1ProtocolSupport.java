package me.tenyks.qiyun.protocol;

import io.netty.buffer.ByteBuf;
import me.tenyks.core.crc.CRCCalculator;
import me.tenyks.core.crc.XORCRCCalculator;
import org.apache.commons.codec.binary.Hex;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;
import org.jetlinks.protocol.common.mapping.ThingItemMapping;
import org.jetlinks.protocol.common.mapping.ThingItemMappings;
import org.jetlinks.protocol.official.binary2.*;
import org.jetlinks.protocol.official.common.DictBook;

/**
 * 云快充新能源汽车充电桩协议
 *
 * 参考：《充电桩与云快充服务平台交互协议》，版本V1.6
 *
 * @author v-lizy81
 * @date 2024/9/9 22:20
 */
public class YKCV1ProtocolSupport {

    public static final String      NAME_AND_VER = "YKC_V1.6";

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

        fieldDcl = buildDataFieldDcl("充电枪数量", "gunCount", BaseDataType.UINT8, (short) (8));
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("通信协议版本", "protocolVersion", BaseDataType.UINT8, (short) (9));
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("程序版本", "firmwareVersion", BaseDataType.CHARS8, (short) (10));
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("网络链接类型", "networkType", BaseDataType.UINT8, (short) (18));
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("SIM卡", "simNo", BaseDataType.BCD10, (short) (19));
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("运营商", "simNo", BaseDataType.UINT8, (short) (29));
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
        structDcl.addField(buildLENFieldDcl((byte) 8));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x02));

        // 数据块
        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("登陆结果", "rstCode", BaseDataType.UINT8, (short)(7));
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
        fieldDcl = buildDataFieldDcl("枪状态", "gunStatus", BaseDataType.UINT8, (short) (8));
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

        fieldDcl = buildDataFieldDcl("心跳应答", "pongFlag", BaseDataType.UINT8, (short) (8));
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

        DefaultFieldDeclaration fieldDcl;

        //首次连接到平台时置零
        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short) (7));
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

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short) (7));
        structDcl.addField(fieldDcl);

        //0x00 桩计费模型与平台一致 0x01 桩计费模型与平台不一致
        fieldDcl = buildDataFieldDcl("验证结果", "rstCode", BaseDataType.UINT8, (short) (9));
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
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x09));

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

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short) (7));
        structDcl.addField(fieldDcl);

        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖费电费费率", "checkRstFlag", BaseDataType.UINT32, (short) (9));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("尖服务费费率", "checkRstFlag", BaseDataType.UINT32, (short) (13));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰电费费率", "checkRstFlag", BaseDataType.UINT32, (short) (17));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("峰服务费费率", "checkRstFlag", BaseDataType.UINT32, (short) (21));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平电费费率", "checkRstFlag", BaseDataType.UINT32, (short) (25));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("平服务费费率", "checkRstFlag", BaseDataType.UINT32, (short) (29));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷电费费率", "checkRstFlag", BaseDataType.UINT32, (short) (33));
        structDcl.addField(fieldDcl);
        // 精确到五位小数
        fieldDcl = buildDataFieldDcl("谷服务费费率", "checkRstFlag", BaseDataType.UINT32, (short) (37));
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("计损比例", "checkRstFlag", BaseDataType.UINT8, (short) (41));
        structDcl.addField(fieldDcl);

        for (int i = 0; i < 24; i++) {
            //0x00：尖费率 0x01：峰费率 0x02：平费率 0x03：谷费率
            fieldDcl = buildDataFieldDcl(String.format("%02d:00～%02d:30时段费率号", i, i), "checkRstFlag", BaseDataType.UINT8, (short) (42 + i));
            structDcl.addField(fieldDcl);
            fieldDcl = buildDataFieldDcl(String.format("%02d:30～%02d:00时段费率号", i, i + 1), "checkRstFlag", BaseDataType.UINT8, (short) (43 + i));
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

        fieldDcl = buildDataFieldDcl("交易流水号", "transNo", BaseDataType.CHARS16, (short)(DATA_BEGIN_IDX));
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

        //整形，偏移量-50；待机置零
        fieldDcl = buildDataFieldDcl("枪线温度", "gunRel", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 15));
        structDcl.addField(fieldDcl);
        //没有置零
        fieldDcl = buildDataFieldDcl("枪线编码", "gunRel", BaseDataType.INT64, (short)(DATA_BEGIN_IDX + 16));
        structDcl.addField(fieldDcl);

        //待机置零；交流桩置零
        fieldDcl = buildDataFieldDcl("SOC", "SOC", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 20));
        structDcl.addField(fieldDcl);

        //整形，偏移量-50 ºC；待机置零； 交流桩置零
        fieldDcl = buildDataFieldDcl("电池组最高温度", "", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + 21));
        structDcl.addField(fieldDcl);
        //单位：min；待机置零
        fieldDcl = buildDataFieldDcl("累计充电时间", "", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 22));
        structDcl.addField(fieldDcl);
        //单位：min；待机置零、交流桩置零
        fieldDcl = buildDataFieldDcl("剩余时间", "", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 24));
        structDcl.addField(fieldDcl);
        //精确到小数点后四位；待机置零
        fieldDcl = buildDataFieldDcl("充电度数", "", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 26));
        structDcl.addField(fieldDcl);
        //精确到小数点后四位；待机置零  未设置计损比例时等于充电度数
        fieldDcl = buildDataFieldDcl("计损充电度数", "", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 30));
        structDcl.addField(fieldDcl);
        //精确到小数点后四位；待机置零 （电费+服务费）* 计损充电度数
        fieldDcl = buildDataFieldDcl("已充金额", "", BaseDataType.UINT32, (short)(DATA_BEGIN_IDX + 34));
        structDcl.addField(fieldDcl);
        //Bit 位表示（0 否 1 是），低位到高位顺序
        //Bit1：急停按钮动作故障；Bit2：无可用整流模块； Bit3：出风口温度过高；Bit4：交流防雷故障；
        //Bit5：交直流模块 DC20 通信中断； Bit6：绝缘检测模块 FC08 通信中断；
        //Bit7：电度表通信中断；Bit8：读卡器通信中断； Bit9：RC10 通信中断；Bit10：风扇调速板故障；
        //Bit11：直流熔断器故障；Bit12：高压接触器故障；Bit13：门打开；
        fieldDcl = buildDataFieldDcl("硬件故障", "", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 38));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电握手[上行], 0x15
     * <li>主动请求</li>
     * <li>GBT-27930 充电桩与BMS充电握手阶段报文</li>
     */
    private static DefaultStructDeclaration buildHandshakeStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电握手[上行]", "CMD:0x15");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("Handshake"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x15));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //当前版本为 V1.1，表示为：byte3，byte2—0001H；byte1—01H
        fieldDcl = buildDataFieldDcl("BMS 通信协议版本号", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);
        //电池类型,01H:铅酸电池;02H:氢电池;03H:磷酸铁锂电池;04H:锰酸锂电池;05H:钴酸锂电池;06H:三元材料电池;07H:聚合物锂离子电池;08H:钛酸锂电池;FFH:其他;
        fieldDcl = buildDataFieldDcl("BMS 电池类型", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);
        //0.1 Ah/位，0 Ah 偏移量
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池系统额定容量", "", BaseDataType.UINT16, (short) 28);
        structDcl.addField(fieldDcl);
        //0.1V/位，0V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池系统额定总电压", "", BaseDataType.UINT16, (short) 30);
        structDcl.addField(fieldDcl);
        //标准 ASCII 码
        fieldDcl = buildDataFieldDcl("BMS 电池生产厂商名称", "", BaseDataType.CHARS4, (short) 34);
        structDcl.addField(fieldDcl);
        //预留，由厂商自行定义
        fieldDcl = buildDataFieldDcl("BMS 电池组序号", "", BaseDataType.HEX_STR_4, (short) 38);
        structDcl.addField(fieldDcl);
        //1985 年偏移量，数据范围：1985～ 2235 年
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期年", "", BaseDataType.UINT8, (short) 42);
        structDcl.addField(fieldDcl);
        //0 月偏移量，数据范围：1～12 月
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期月", "", BaseDataType.UINT8, (short) 43);
        structDcl.addField(fieldDcl);
        //0 日偏移量，数据范围：1～31 日
        fieldDcl = buildDataFieldDcl("BMS 电池组生产日期日", "", BaseDataType.UINT8, (short) 44);
        structDcl.addField(fieldDcl);
        //1次/位，0次偏移量，以BMS统计为准
        fieldDcl = buildDataFieldDcl("BMS 电池组充电次数", "", BaseDataType.UINT24, (short) 45);
        structDcl.addField(fieldDcl);
        //0=租赁；1=车自有
        fieldDcl = buildDataFieldDcl("BMS 电池组产权标识", "", BaseDataType.UINT8, (short) 46);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT8, (short) 47);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("BMS 车辆识别码", "VIN", BaseDataType.CHARS16, (short) 24);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("BMS 软件版本号", "", BaseDataType.HEX_STR_8, (short) 47);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 参数配置[上行], 0x17
     * <li>主动请求</li>
     * <li>GBT-27930 充电桩与 BMS 参数配置阶段报文</li>
     */
    private static DefaultStructDeclaration buildReportSettingStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("参数配置[上行]", "CMD:0x17");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("Handshake"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x15));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 0.01 V/位，0 V 偏移量； 数据范围：0~24 V
        fieldDcl = buildDataFieldDcl("BMS 单体动力蓄电池最高允许充电电压", "", BaseDataType.UINT16, (short) 24);
        structDcl.addField(fieldDcl);

        // 0.1 A/位，-400A 偏移量
        fieldDcl = buildDataFieldDcl("BMS 最高允许充电电流", "", BaseDataType.UINT16, (short) 26);
        structDcl.addField(fieldDcl);

        // 0.1 kWh/位，0 kWh 偏移量； 数据范围：0~1000 kWh
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池标称总能量", "", BaseDataType.UINT16, (short) 28);
        structDcl.addField(fieldDcl);

        // 0.1 V/位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("BMS 最高允许充电总电压", "", BaseDataType.UINT16, (short) 30);
        structDcl.addField(fieldDcl);

        //1ºC/位，-50 ºC 偏移量；数据范 围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 最高允许温度", "", BaseDataType.UINT8, (short) 32);
        structDcl.addField(fieldDcl);

        //0.1%/位，0%偏移量；数据范围：0 ~ 100%
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池荷电状态(soc)", "SOC", BaseDataType.UINT16, (short) 33);
        structDcl.addField(fieldDcl);

        //整车动力蓄电池总电压
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池当前电池电压", "", BaseDataType.UINT16, (short) 35);
        structDcl.addField(fieldDcl);

        //0.1 V /位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("电桩最高输出电压", "", BaseDataType.UINT16, (short) 37);
        structDcl.addField(fieldDcl);

        //0.1 V /位，0 V 偏移量
        fieldDcl = buildDataFieldDcl("电桩最低输出电压", "", BaseDataType.UINT16, (short) 39);
        structDcl.addField(fieldDcl);

        //0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("电桩最大输出电流", "", BaseDataType.UINT16, (short) 41);
        structDcl.addField(fieldDcl);

        //0.1 A/位，-400 A 偏移量
        fieldDcl = buildDataFieldDcl("电桩最小输出电流", "", BaseDataType.UINT16, (short) 43);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电结束[上行], 0x19
     * <li>主动请求</li>
     * <li>GBT-27930 充电桩与 BMS 充电结束阶段报文</li>
     */
    private static DefaultStructDeclaration buildChargingEndEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电结束[上行]", "CMD:0x19");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ChargingEndEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x19));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //1%/位，0%偏移量；数据范围：0~100%
        fieldDcl = buildDataFieldDcl("BMS 中止荷电状态 SOC", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //0.01 V/位，0 V 偏移量；数据范 围：0 ~24 V
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池单体最低电压", "", BaseDataType.UINT16, (short) 25);
        structDcl.addField(fieldDcl);

        //0.01 V/位，0 V 偏移量；数据范 围：0 ~24 V
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池单体最高电压", "", BaseDataType.UINT16, (short) 27);
        structDcl.addField(fieldDcl);

        //1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池最低温度", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        //1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池最高温度", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);

        //1 min/位，0 min 偏移量；数据范围：0~600 min
        fieldDcl = buildDataFieldDcl("电桩累计充电时间", "", BaseDataType.UINT16, (short) 31);
        structDcl.addField(fieldDcl);

        //0.1 kWh/位，0 kWh 偏移量；数据范围：0~1000 kWh
        fieldDcl = buildDataFieldDcl("电桩输出能量", "", BaseDataType.UINT16, (short) 33);
        structDcl.addField(fieldDcl);

        //充电机编号， 1/位， 1偏移量 ，数 据范 围 ： 0 ～ 0xFFFFFFFF
        fieldDcl = buildDataFieldDcl("", "", BaseDataType.UINT32, (short) 34);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 错误报文[上行], 0x1B
     * <li>主动请求</li>
     * <li>GBT-27930 充电桩与 BMS 充电错误报文</li>
     */
    private static DefaultStructDeclaration buildReportErrorEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("错误报文[上行]", "CMD:0x1B");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportErrorEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x19));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收SPN2560=0x00的充电机辨识报文超时", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收SPN2560=0xAA的充电机辨识报文超时", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("预留位", "RF01", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收充电机的时间同步和充电机最大输出能力报文超时", "", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收充电机完成充电准备报文超时", "", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("预留位", "RF02", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收充电机充电状态报文超时", "", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收充电机中止充电报文超时", "", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("预留位", "RF03", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收充电机充电统计报文超时", "", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("BMS其他", "", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收BMS和车辆的辨识报文超时", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收电池充电参数报文超时", "", BaseDataType.UINT8, (short) 28);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收BMS完成充电准备报文超时", "", BaseDataType.UINT8, (short) 28);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT8, (short) 28);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收电池充电总状态报文超时", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收电池充电要求报文超时", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收BMS中止充电报文超时", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("接收BMS充电统计报文超时", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);

        //<00>：=正常；<01>：=超时；<10>： =不可信状态
        fieldDcl = buildDataFieldDcl("充电机其他", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电阶段BMS中止[上行], 0x1D
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电阶段 BMS 中止报文</li>
     */
    private static DefaultStructDeclaration buildReportBMSStopEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电阶段BMS中止[上行]", "CMD:0x1D");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportErrorEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x1D));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //1-2 位——所需求的 SOC 目标值3-4 位——达到总电压的设定值5-6 位——达到单体电压设定值7-8 位——充电机主动中止
        fieldDcl = buildDataFieldDcl("BMS中止充电原因", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //1-2位——绝缘故障
        //3-4位——输出连接器过温故障5-6 位——BMS 元件、输出连接器过温
        //7-8位——充电连接器故障
        //9-10位——电池组温度过高故障
        //11-12位——高压继电器故障
        //13-14位——检测点2电压检测故障
        //15-16位——其他故障
        fieldDcl = buildDataFieldDcl("BMS中止充电故障原因", "", BaseDataType.UINT16, (short) 25);
        structDcl.addField(fieldDcl);

        //1-2位——电流过大  3-4位——电压异常  5-8位——预留位
        fieldDcl = buildDataFieldDcl("BMS中止充电错误原因", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电阶段充电机中止[上行], 0x21
     * <li>主动上送</li>
     * <li>GBT-27930 充电桩与 BMS 充电阶段充电机中止报文</li>
     */
    private static DefaultStructDeclaration buildReportChargerStopEventStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电阶段BMS中止[上行]", "CMD:0x21");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportChargerStopEvent"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x21));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //1-2 位——达到充电机设定的条件中止
        //3-4 位——人工中止
        //5-6 位——异常中止
        //7-8 位——BMS 主动中止
        fieldDcl = buildDataFieldDcl("充电机中止充电原因", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //1-2 位——充电机过温故障
        //3-4 位——充电连接器故障
        //5-6 位——充电机内部过温故障
        //7-8 位——所需电量不能传送
        //9-10 位——充电机急停故障
        //11-12 位——其他故障
        //13-16 位——预留位
        fieldDcl = buildDataFieldDcl("充电机中止充电故障原因", "", BaseDataType.UINT16, (short) 25);
        structDcl.addField(fieldDcl);

        //1-2位——电流不匹配 3-4位——电压异常 5-8位——预留位
        fieldDcl = buildDataFieldDcl("充电机中止充电错误原因", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电过程BMS需求与充电机输出[上行], 0x23
     * <li>周期上送（15 秒）</li>
     * <li>GBT-27930 充电桩与BMS充电过程BMS需求、充电机输出</li>
     */
    private static DefaultStructDeclaration buildReportBMSRequirementAndChargerOutputDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电过程BMS需求与充电机输出[上行]", "CMD:0x23");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportBMSRequirementAndChargerOutputData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x23));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        //1-2 位——达到充电机设定的条件中止
        //3-4 位——人工中止
        //5-6 位——异常中止
        //7-8 位——BMS 主动中止
        fieldDcl = buildDataFieldDcl("充电机中止充电原因", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //1-2 位——充电机过温故障
        //3-4 位——充电连接器故障
        //5-6 位——充电机内部过温故障
        //7-8 位——所需电量不能传送
        //9-10 位——充电机急停故障
        //11-12 位——其他故障
        //13-16 位——预留位
        fieldDcl = buildDataFieldDcl("充电机中止充电故障原因", "", BaseDataType.UINT16, (short) 25);
        structDcl.addField(fieldDcl);

        //1-2位——电流不匹配 3-4位——电压异常 5-8位——预留位
        fieldDcl = buildDataFieldDcl("充电机中止充电错误原因", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电过程BMS信息[上行], 0x25
     * <li>周期上送（15 秒）</li>
     * <li>GBT-27930 充电桩与BMS充电过程BMS信息</li>
     */
    private static DefaultStructDeclaration buildReportBMSDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电过程BMS信息[上行]", "CMD:0x25");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportBMSData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x25));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 1/位，1 偏移量；数据范围：1~256
        fieldDcl = buildDataFieldDcl("BMS 最高单体动力蓄电池电压所在编号", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        // 1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~ +200 ºC
        fieldDcl = buildDataFieldDcl("BMS 最高动力蓄电池温度", "", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        // 1/位，1 偏移量；数据范围：1~128
        fieldDcl = buildDataFieldDcl("最高温度检测点编号", "", BaseDataType.UINT8, (short) 26);
        structDcl.addField(fieldDcl);

        // 1ºC/位，-50 ºC 偏移量；数据范围：-50 ºC ~+200 ºC
        fieldDcl = buildDataFieldDcl("最低动力蓄电池温度", "", BaseDataType.UINT8, (short) 27);
        structDcl.addField(fieldDcl);

        // 1/位，1 偏移量；数据范围：1~128
        fieldDcl = buildDataFieldDcl("最低动力蓄电池温度检测点编号", "", BaseDataType.UINT8, (short) 28);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 单体动力蓄电池电压过高/过低", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 整车动力蓄电池荷电状态SOC过高/过低", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池充电过电流", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池温度过高", "", BaseDataType.UINT8, (short) 29);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池绝缘状态", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("BMS 动力蓄电池组输出连接器连接状态", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);

        // <00>：=正常; <01>：=过高; <10>： =过低
        fieldDcl = buildDataFieldDcl("充电禁止", "", BaseDataType.UINT8, (short) 30);
        structDcl.addField(fieldDcl);

        // 默认填00
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 充电桩主动申请启动充电[上行], 0x31
     * <li>按需发送</li>
     * <li>用户通过帐号密码及刷卡在充电桩上操作请求充电</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOnRequestStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("充电过程BMS信息[上行]", "CMD:0x31");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportBMSData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x31));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo((short) 0));
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        //0x01 表示通过刷卡启动充电
        //0x02 表求通过帐号启动充电（暂不支持）
        //0x03 表示vin码启动充电
        fieldDcl = buildDataFieldDcl("启动方式", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        // 0x00 不需要 0x01 需要
        fieldDcl = buildDataFieldDcl("是否需要密码", "", BaseDataType.UINT8, (short) 9);
        structDcl.addField(fieldDcl);

        // 不足 8 位补 0，具体见示例
        fieldDcl = buildDataFieldDcl("账号或者物理卡号", "", BaseDataType.CHARS8, (short) 10);
        structDcl.addField(fieldDcl);

        // 对用户输入的密码进行16位MD5加密，采用小写上传
        fieldDcl = buildDataFieldDcl("输入密码", "", BaseDataType.HEX_STR_8, (short) 18);
        structDcl.addField(fieldDcl);

        //启动方式为vin码启动充电时上送, 其他方式置零( ASCII码)，VIN码需要反序上送
        fieldDcl = buildDataFieldDcl("VIN码", "", BaseDataType.CHARS17, (short) 26);
        structDcl.addField(fieldDcl);


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 运营平台确认启动充电[下行], 0x32
     * <li>应答</li>
     * <li>启动充电鉴权结果</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOnResponseStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台确认启动充电[下行]", "CMD:0x32");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PileSwitchOnResponse"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x32));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 显示在屏幕上，不足 8 位补零
        fieldDcl = buildDataFieldDcl("逻辑卡号", "", BaseDataType.CHARS8, (short) 24);
        structDcl.addField(fieldDcl);

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("账户余额", "", BaseDataType.INT32, (short) 32);
        structDcl.addField(fieldDcl);

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("鉴权成功标志", "", BaseDataType.UINT8, (short) 36);
        structDcl.addField(fieldDcl);

        //0x01 账户不存在
        //0x02 账户冻结
        //0x03 账户余额不足
        //0x04 该卡存在未结账记录
        //0x05 桩停用
        //0x06 该账户不能在此桩上充电
        //0x07 密码错误
        //0x08 电站电容不足
        //0x09 系统中 vin 码不存在0x0A 该桩存在未结账记录0x0B 该桩不支持刷卡
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, (short) 37);
        structDcl.addField(fieldDcl);


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 运营平台远程控制启机命令[下行], 0x34
     * <li>按需发送</li>
     * <li>当用户通过远程启动充电时，发送本命令</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOnFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台远程控制启机命令[下行]", "CMD:0x34");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PileSwitchOnFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x34));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 显示在屏幕上，不足 8 位补零
        fieldDcl = buildDataFieldDcl("逻辑卡号", "", BaseDataType.CHARS8, (short) 24);
        structDcl.addField(fieldDcl);

        // 不足补零，桩与平台交互需使用的物理卡号
        fieldDcl = buildDataFieldDcl("物理卡号", "", BaseDataType.HEX_STR_8, (short) 32);
        structDcl.addField(fieldDcl);

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("账户余额", "", BaseDataType.INT32, (short) 40);
        structDcl.addField(fieldDcl);


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程启动充电命令回复[上行], 0x33
     * <li>应答</li>
     * <li>启动充电鉴权结果</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOnFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程启动充电命令回复[上行]", "CMD:0x33");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PileSwitchOnFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x33));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        // 0x00失败 0x01成功
        fieldDcl = buildDataFieldDcl("启动结果", "", BaseDataType.UINT8, (short) 24);
        structDcl.addField(fieldDcl);

        //0x00 无
        //0x01 设备编号不匹配
        //0x02 枪已在充电
        //0x03 设备故障
        //0x04 设备离线
        //0x05 未插枪 桩在收到启充命令后,检测到未插枪则发送
        //0x33 报文回复充电失败。若在 60 秒（以收到 0x34 时间开始计算）内检测到枪重新连接，则补送 0x33 成功报文;超时或者离线等其他异常，桩不启充、不补发 0x33 报文
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 运营平台远程停机[下行], 0x36
     * <li>按需发送</li>
     * <li>当用户通过远程停止充电时，发送本命令，如APP停止充电</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOffFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("运营平台远程停机[下行]", "CMD:0x36");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PileSwitchOffFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x34));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo((short) 0));
        structDcl.addField(buildDFDclOfGunNo((short) 7));


        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程停机命令回复[上行], 0x33
     * <li>应答发送</li>
     * <li>远程停止充电命令回复，平台发送 0x36 后即关闭订单，接收到停机指令后设备务必保证停机。</li>
     */
    private static DefaultStructDeclaration buildPileSwitchOffFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程停机命令回复[上行]", "CMD:0x35");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("PileSwitchOffFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x35));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo((short) 0));
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        // 0x00失败 0x01成功
        fieldDcl = buildDataFieldDcl("停止结果", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        //0x00 无
        //0x01 设备编号不匹配0x02 枪未处于充电状态
        //0x03 其他
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, (short) 25);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 上报交易记录[上行], 0x3B
     * <li>主动上送</li>
     * <li>充电桩在网络正常情况下，主运发送结算账单，直到运营平台响应成账单上传成功
     * （若未收到 0x40 回复间隔 30s 再重试一次，最多重试 3 次），收到账单结算成功，本账单在充电桩本地删除。
     * 每次接收到启机命令并已执行启机过程，无论启机成功与否，都需在订单结束充电后生成账单上传</li>
     */
    private static DefaultStructDeclaration buildReportTransOrderStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("上报交易记录[上行]", "CMD:0x3B");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportTransOrder"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x35));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());
        structDcl.addField(buildDFDclOfPileNo((short) 16));
        structDcl.addField(buildDFDclOfGunNo((short) 23));

        fieldDcl = buildDataFieldDcl("开始时间", "", BaseDataType.CP56Time2a, (short) 24);
        structDcl.addField(fieldDcl);

        fieldDcl = buildDataFieldDcl("结束时间", "", BaseDataType.CP56Time2a, (short) 31);
        structDcl.addField(fieldDcl);

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("尖单价", "", BaseDataType.UINT32, (short) 38);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("尖电量", "", BaseDataType.UINT32, (short) 42);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损尖电量", "", BaseDataType.UINT32, (short) 46);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("尖金额", "", BaseDataType.UINT32, (short) 50);
        structDcl.addField(fieldDcl);

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("峰单价", "", BaseDataType.UINT32, (short) 54);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("峰电量", "", BaseDataType.UINT32, (short) 58);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损峰电量", "", BaseDataType.UINT32, (short) 62);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("峰金额", "", BaseDataType.UINT32, (short) 66);
        structDcl.addField(fieldDcl);

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("平单价", "", BaseDataType.UINT32, (short) 70);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("平电量", "", BaseDataType.UINT32, (short) 74);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损平电量", "", BaseDataType.UINT32, (short) 78);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("平金额", "", BaseDataType.UINT32, (short) 82);
        structDcl.addField(fieldDcl);

        //精确到小数点后五位（尖电费+尖服务费，见费率帧）
        fieldDcl = buildDataFieldDcl("谷单价", "", BaseDataType.UINT32, (short) 86);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("谷电量", "", BaseDataType.UINT32, (short) 90);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损谷电量", "", BaseDataType.UINT32, (short) 94);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("谷金额", "", BaseDataType.UINT32, (short) 98);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("电表总起值", "", BaseDataType.UINT40, (short) 102);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("电表总止值", "", BaseDataType.UINT40, (short) 107);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("总电量", "", BaseDataType.UINT32, (short) 112);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("计损总电量", "", BaseDataType.UINT32, (short) 116);
        structDcl.addField(fieldDcl);

        //精确到小数点后四位
        fieldDcl = buildDataFieldDcl("消费金额", "", BaseDataType.UINT32, (short) 120);
        structDcl.addField(fieldDcl);

        //VIN 码，此处 VIN 码和充电时 VIN码不同，正序直接上传，无需补 0和反序
        fieldDcl = buildDataFieldDcl("电动汽车唯一标识", "", BaseDataType.CHARS17, (short) 124);
        structDcl.addField(fieldDcl);

        //0x01：app 启动0x02：卡启动
        //0x04：离线卡启动
        //0x05: vin 码启动充电
        fieldDcl = buildDataFieldDcl("交易标识", "", BaseDataType.UINT8, (short) 141);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("交易时间", "", BaseDataType.CP56Time2a, (short) 142);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("停止原因", "", BaseDataType.UINT8, (short) 149);
        structDcl.addField(fieldDcl);

        //
        fieldDcl = buildDataFieldDcl("物理卡号", "", BaseDataType.HEX_STR_8, (short) 150);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 交易记录确认[下行], 0x40
     * <li>应答发送</li>
     * <li>运营平台接收到结算账单上传后，都需回复此确认信息。若桩未收到回复帧，则 5 分钟后继续
     * 上送一次交易记录，此情况下无论平台是否成功回复都停止上送。
     * 这一帧仅是报文交互使用，意指平台成功接收到交易记录报文，并不代表交易订单成功结算</li>
     */
    private static DefaultStructDeclaration buildReportTransOrderAckStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("交易记录确认[下行]", "CMD:0x40");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportTransOrderAck"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x40));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfTransNo());

        //0x00 上传成功 0x01 非法账单
        fieldDcl = buildDataFieldDcl("确认结果", "", BaseDataType.UINT8, (short) 16);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程账户余额更新[下行], 0x42
     * <li>按需发送</li>
     * <li>平台在用户完成充值后会将用户更新的余额下发到充电桩，桩接收到此数据帧需要对当前充电用户的信息进行校验并更新余额信息</li>
     */
    private static DefaultStructDeclaration buildUpdateAccountBalanceFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("交易记录确认[下行]", "CMD:0x42");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("UpdateAccountBalanceFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x42));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        // 不足 8 位补零
        // 如果不为零，需要校验本次充电是否为此卡充电
        // 如果为零，则不校验，直接更新桩当前充电用户余额
        fieldDcl = buildDataFieldDcl("物理卡号", "", BaseDataType.HEX_STR_8, (short) 8);
        structDcl.addField(fieldDcl);

        // 保留两位小数
        fieldDcl = buildDataFieldDcl("修改后账户金额", "", BaseDataType.UINT32, (short) 16);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 余额更新应答[上行], 0x41
     * <li>应答回复</li>
     * <li>平台在用户完成充值后会将用户更新的余额下发到充电桩，桩接收到此数据帧需要对当前充电用户的信息进行校验并更新余额信息</li>
     */
    private static DefaultStructDeclaration buildUpdateAccountBalanceFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("余额更新应答[上行]", "CMD:0x41");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("UpdateAccountBalanceFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x41));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());

        // 不足 8 位补零
        // 如果不为零，需要校验本次充电是否为此卡充电
        // 如果为零，则不校验，直接更新桩当前充电用户余额
        fieldDcl = buildDataFieldDcl("物理卡号", "", BaseDataType.HEX_STR_8, (short) 7);
        structDcl.addField(fieldDcl);

        //0x00-修改成功 0x01-设备编号错误 0x02-卡号错误
        fieldDcl = buildDataFieldDcl("修改结果", "", BaseDataType.UINT8, (short) 15);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 下发离线卡数据指令[下行], 0x44
     * <li>按需发送</li>
     * <li>离线卡适用于桩离线充电模式，平台在充电桩在线时会下发此数据帧到充电桩，
     * 充电桩接收到后储存离线卡信息到桩本地（如果已存在离线卡则用最新的数据覆盖本地数据，不存在则插入），
     * 若用户刷卡充电时桩处理离线模式，则刷鉴权走桩本地进行判断。</li>
     */
    private static DefaultStructDeclaration buildWriteICDataFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("下发离线卡数据指令[下行]", "CMD:0x44");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteICDataFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x44));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl);

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("端口X的状况", "portXState", (short)5, (short)8);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
        groupDcl.setInstancePostProcessor((idx, fieldInstances) -> {
            int portNo = 0;
            for (FieldInstance fInst : fieldInstances) {
                if (fInst.getCode().equals("PortNo")) {
                    portNo = fInst.getIntValue();
                }
            }

            String prefix = String.format("port%d", portNo);
            fieldInstances.forEach(v -> ((AbstractFieldInstance) v).setCodePrefix(prefix));

            return fieldInstances;
        });

        DynamicAnchor anchor = groupDcl.asAnchor();

        DefaultFieldDeclaration fieldDcl;

        //
        fieldDcl = buildDataFieldDcl("第X个卡逻辑卡号", "", BaseDataType.CHARS8, anchor, (short) 0);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX_STR_8, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 下发离线卡数据指令响应[上行], 0x43
     * <li>应答回复</li>
     * <li>离线卡数据同步应答</li>
     */
    private static DefaultStructDeclaration buildWriteICDataFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("下发离线卡数据指令响应[上行]", "CMD:0x43");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteICDataFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x43));

        // 数据块
        DefaultFieldDeclaration fieldDcl;

        structDcl.addField(buildDFDclOfPileNo());

        //0x01 卡号格式错误 0x02 储存空间不足
        fieldDcl = buildDataFieldDcl("保存结果", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        //0x01 卡号格式错误 0x02 储存空间不足
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, (short) 9);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 清除离线卡数据指令[下行], 0x46
     * <li>按需发送</li>
     * <li>离线卡清除是平台主动下发的操作，平台在充电桩在线时会下发此数据帧到充电桩，
     *      充电桩接收到离线卡数据清除报文后清除到桩本地对应的离线卡数据</li>
     */
    private static DefaultStructDeclaration buildCleanICDataFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("清除离线卡数据指令[下行]", "CMD:0x46");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CleanICDataFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x46));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl);

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("目标卡X", "cardX", (short)5, (short)8);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
        groupDcl.setInstancePostProcessor((idx, fieldInstances) -> {
            int portNo = 0;
            for (FieldInstance fInst : fieldInstances) {
                if (fInst.getCode().equals("PortNo")) {
                    portNo = fInst.getIntValue();
                }
            }

            String prefix = String.format("port%d", portNo);
            fieldInstances.forEach(v -> ((AbstractFieldInstance) v).setCodePrefix(prefix));

            return fieldInstances;
        });

        DynamicAnchor anchor = groupDcl.asAnchor();

        DefaultFieldDeclaration fieldDcl;

        //
        fieldDcl = buildDataFieldDcl("第X个卡逻辑卡号", "", BaseDataType.CHARS8, anchor, (short) 0);
        structDcl.addField(fieldDcl);
        //
        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX_STR_8, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 清除离线卡数据指令响应[上行], 0x45
     * <li>应答回复</li>
     * <li>离线卡数据清除应答</li>
     */
    private static DefaultStructDeclaration buildClearICDataFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("清除离线卡数据指令响应[上行]", "CMD:0x45");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CleanICDataFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x45));

        //
        DefaultFieldDeclaration countFieldDcl;
        countFieldDcl = buildDataFieldDcl("下发卡个数", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(countFieldDcl); //TODO 通过数据区长度求解卡的个数

        DefaultNRepeatFieldGroupDeclaration groupDcl;
        groupDcl = new DefaultNRepeatFieldGroupDeclaration("条目", "itemX", (short) 7, (short) 10);
        groupDcl.setDynamicNRepeat(countFieldDcl.asDynamicNRepeat());
        groupDcl.setAnchorReference(countFieldDcl.asAnchor(), (short) 0);
        groupDcl.setInstancePostProcessor((idx, fieldInstances) -> {
            int portNo = 0;
            for (FieldInstance fInst : fieldInstances) {
                if (fInst.getCode().equals("PortNo")) {
                    portNo = fInst.getIntValue();
                }
            }

            String prefix = String.format("port%d", portNo);
            fieldInstances.forEach(v -> ((AbstractFieldInstance) v).setCodePrefix(prefix));

            return fieldInstances;
        });

        DynamicAnchor anchor = groupDcl.asAnchor();

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("第X个卡物理卡号", "", BaseDataType.HEX_STR_8, anchor, (short) 8);
        structDcl.addField(fieldDcl);

        // 0x00 清除失败 0x01 清除成功
        fieldDcl = buildDataFieldDcl("清除标记", "", BaseDataType.UINT8, anchor, (short) 0);
        structDcl.addField(fieldDcl);

        //0x01 卡号格式错误 0x02 清除成功
        fieldDcl = buildDataFieldDcl("失败原因", "", BaseDataType.UINT8, anchor, (short) 0);
        structDcl.addField(fieldDcl);

        structDcl.addGroup(groupDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写充电桩工作参数设置[下行], 0x52
     * <li>按需下发</li>
     * <li>远程设置充电桩是否停用；设置充电桩允许输出功率，以实现电网功率的调节</li>
     */
    private static DefaultStructDeclaration buildWritePileSettingsFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写充电桩工作参数设置[下行]", "CMD:0x52");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WritePileSettingsFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x52));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("是否允许工作", "", BaseDataType.HEX_STR_8, (short) 7);
        structDcl.addField(fieldDcl);

        // 1BIN 表示 1%，最大 100%，最小30%
        fieldDcl = buildDataFieldDcl("充电桩最大允许输出功率", "", BaseDataType.HEX_STR_8, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写充电桩工作参数设置响应[上行], 0x51
     * <li>按需发送</li>
     * <li>充电桩接收到运营平台充电桩工作参数设置时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildWritePileSettingsFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写充电桩工作参数设置响应[上行]", "CMD:0x51");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WritePileSettingsFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x51));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "", BaseDataType.HEX_STR_8, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写对时设置[下行], 0x56
     * <li>周期发送（1 天）</li>
     * <li>运营平台同步充电桩时钟，以保证充电桩与运营平台的时钟一致</li>
     */
    private static DefaultStructDeclaration buildWriteDateTimeFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写对时设置[下行]", "CMD:0x56");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteDateTimeFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x56));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("当前时间", "", BaseDataType.CP56Time2a, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写对时设置响应[上行], 0x55
     * <li>应答</li>
     * <li>充电桩接收到运营平台同步充电桩时钟时应答</li>
     */
    private static DefaultStructDeclaration buildWriteDateTimeFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写对时设置响应[上行]", "CMD:0x55");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteDateTimeFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x55));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 表示允许正常工作 0x01 表示停止使用，锁定充电桩
        fieldDcl = buildDataFieldDcl("当前时间", "", BaseDataType.CP56Time2a, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 写计费模型设置[下行], 0x58
     * <li>按需发送</li>
     * <li>用户充电费用计算，每半小时为一个费率段，共48段，每段对应尖峰平谷其中一个费率充电时桩屏幕按此费率分别显示已充电费和服务费</li>
     */
    private static DefaultStructDeclaration buildWriteFeeTermsStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写对时设置[下行]", "CMD:0x56");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteFeeTerms"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x56));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("计费模型编号", "termsNo", BaseDataType.UINT16, (short)(DATA_BEGIN_IDX + 7));
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
     * 写计费模型设置响应[上行], 0x57
     * <li>应答</li>
     * <li>充电桩接收到运营平台计费模型时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildWriteFeeTermsReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("写计费模型设置响应[上行]", "CMD:0x57");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("WriteFeeTermsReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x55));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "", BaseDataType.UINT8, (short) 7);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 地锁数据上送[上行], 0x61
     * <li>应答</li>
     * <li>充电桩接收到运营平台计费模型时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildReportParkLockDataStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("地锁数据上送[上行]", "CMD:0x61");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ReportParkLockData"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x61));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        // 0x00：未到位状态 0x55：升锁到位状态 0xFF：降锁到位状态
        fieldDcl = buildDataFieldDcl("车位锁状态", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        // 0x00：无车辆 0xFF：停放车辆
        fieldDcl = buildDataFieldDcl("车位状态", "", BaseDataType.UINT8, (short) 9);
        structDcl.addField(fieldDcl);

        // 百分比值0~100
        fieldDcl = buildDataFieldDcl("地锁电量状态", "", BaseDataType.UINT8, (short) 10);
        structDcl.addField(fieldDcl);

        //0x00：正常无报警
        //0xFF：待机状态摇臂破坏
        //0x55：摇臂升降异常(未到位)
        fieldDcl = buildDataFieldDcl("报警状态", "", BaseDataType.UINT8, (short) 11);
        structDcl.addField(fieldDcl);

        // 全部置0
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short) 12);
        structDcl.addField(fieldDcl.setDefaultValue(0));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 遥控地锁升锁与降锁命令[下行], 0x62
     * <li>按需发送</li>
     * <li>服务器下发命令给地锁，地锁执行动作</li>
     */
    private static DefaultStructDeclaration buildParkLockControlFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("遥控地锁升锁与降锁命令[下行]", "CMD:0x62");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ParkLockControlFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x62));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        fieldDcl = buildDataFieldDcl("升/降地锁", "", BaseDataType.UINT8, (short)(8));
        structDcl.addField(fieldDcl);

        // 全部置 0（可用于多枪）
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short)(9));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 遥控地锁升锁与降锁命令响应[上行], 0x63
     * <li>按需发送</li>
     * <li>地锁收到遥控地锁升锁与降锁命令指令，响应本数据</li>
     */
    private static DefaultStructDeclaration buildParkLockControlFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("遥控地锁升锁与降锁命令响应[上行]", "CMD:0x63");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ParkLockControlFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x63));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());
        structDcl.addField(buildDFDclOfGunNo((short) 7));

        DefaultFieldDeclaration fieldDcl;

        // 1，鉴权成功；0，鉴权失败
        fieldDcl = buildDataFieldDcl("地锁控制返回标志", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        // 全部置0
        fieldDcl = buildDataFieldDcl("预留位", "", BaseDataType.UINT32, (short) 9);
        structDcl.addField(fieldDcl.setDefaultValue(0));

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程重启命令[下行], 0x92
     * <li>按需发送</li>
     * <li>重启充电桩，应对部分问题，如卡死</li>
     */
    private static DefaultStructDeclaration buildRebootFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程重启命令[下行]", "CMD:0x92");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("RebootFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x92));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        //0x01：立即执行  0x02：空闲执行
        fieldDcl = buildDataFieldDcl("执行控制", "", BaseDataType.UINT8, (short)(7));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程重启应答[上行], 0x91
     * <li>按需发送</li>
     * <li>充电桩接收到运营平台远程重启指令时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildRebootFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程重启应答[上行]", "CMD:0x91");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ParkLockControlFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x91));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00 失败 0x01 成功
        fieldDcl = buildDataFieldDcl("设置结果", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程更新命令[下行], 0x94
     * <li>按需发送</li>
     * <li>对桩进行软件升级，平台升级模式为 ftp 文件升级，由桩企提供升级需要的更新文件（特定文件名，由桩企定义），
     * 平台在数据帧中提供访问更新文件相关服务器地址及下载路径信息，桩下载完更新程序后对文件进行较验，并对桩进行升级。</li>
     */
    private static DefaultStructDeclaration buildCallOfOTAFunInvStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程更新命令[下行]", "CMD:0x94");

        structDcl.enableEncode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("CallOfOTAFunInv"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x94));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        //0x01：直流  0x02：交流
        fieldDcl = buildDataFieldDcl("桩型号", "", BaseDataType.UINT8, (short)(7));
        structDcl.addField(fieldDcl);

        //不足 2 位补零
        fieldDcl = buildDataFieldDcl("桩功率", "", BaseDataType.UINT16, (short)(8));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("升级服务器地址", "", BaseDataType.CHARS16, (short)(10));
        structDcl.addField(fieldDcl);

        //不足 2 位补零
        fieldDcl = buildDataFieldDcl("升级服务器端口", "", BaseDataType.UINT16, (short)(26));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("用户名", "", BaseDataType.CHARS16, (short)(28));
        structDcl.addField(fieldDcl);

        //不足 16 位补零
        fieldDcl = buildDataFieldDcl("密码", "", BaseDataType.CHARS16, (short)(44));
        structDcl.addField(fieldDcl);

        //不足 32 位补零，文件路径名由平台定义
        fieldDcl = buildDataFieldDcl("文件路径", "", BaseDataType.CHARS32, (short)(60));
        structDcl.addField(fieldDcl);

        //0x01：立即执行 0x02：空闲执行
        fieldDcl = buildDataFieldDcl("执行控制", "", BaseDataType.UINT8, (short)(92));
        structDcl.addField(fieldDcl);

        //单位：min
        fieldDcl = buildDataFieldDcl("下载超时时间", "", BaseDataType.UINT8, (short)(93));
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }

    /**
     * 远程重启应答[上行], 0x93
     * <li>按需发送</li>
     * <li>充电桩接收到运营平台远程重启指令时，响应本数据</li>
     */
    private static DefaultStructDeclaration buildCallOfOTAFunInvReplyStructDcl() {
        DefaultStructDeclaration structDcl = new DefaultStructDeclaration("远程重启应答[上行]", "CMD:0x93");

        structDcl.enableDecode();
        structDcl.addThingAnnotation(ThingAnnotation.Event("ParkLockControlFunInvReply"));

        structDcl.addField(buildSOP());
        structDcl.addField(buildLENFieldDcl((byte) 7));
        structDcl.addField(buildMsgNoFieldDcl());
        structDcl.addField(buildEncyFlagFieldDcl());
        structDcl.addField(buildMsgTypeFieldDcl((byte) 0x93));

        // 数据块

        structDcl.addField(buildDFDclOfPileNo());

        DefaultFieldDeclaration fieldDcl;

        // 0x00-成功  0x01-编号错误    0x02-程序与桩型号不符  0x03-下载更新文件超时
        fieldDcl = buildDataFieldDcl("升级状态", "", BaseDataType.UINT8, (short) 8);
        structDcl.addField(fieldDcl);

        structDcl.addField(buildCRCFieldDcl());
        structDcl.setCRCCalculator(buildCRCCalculator());

        return structDcl;
    }


    /**
     * 数据字段：交易流水号
     */
    private static DefaultFieldDeclaration buildDFDclOfTransNo() {
        return buildDataFieldDcl("交易流水号", "transNo", BaseDataType.CHARS16, DATA_BEGIN_IDX);
    }

    /**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo() {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.CHARS7, DATA_BEGIN_IDX);
    }

    /**
     * 数据字段：桩编码
     */
    private static DefaultFieldDeclaration buildDFDclOfPileNo(short absOffset) {
        return new DefaultFieldDeclaration("桩编码", "pileNo", BaseDataType.CHARS7, (short)(DATA_BEGIN_IDX + absOffset));
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
     * 数据字段：枪号
     */
    private static DefaultFieldDeclaration buildDFDclOfGunNo(short absOffset) {
        return new DefaultFieldDeclaration("枪号", "gunNo", BaseDataType.UINT8, (short)(DATA_BEGIN_IDX + absOffset));
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

    private static DefaultFieldDeclaration buildDataFieldDcl(String name, String code, BaseDataType dataType, short absOffsetInData) {
        return new DefaultFieldDeclaration(name, code, dataType, (short)(DATA_BEGIN_IDX + absOffsetInData));
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
