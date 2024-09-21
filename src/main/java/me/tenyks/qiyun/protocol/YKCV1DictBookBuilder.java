package me.tenyks.qiyun.protocol;

import org.jetlinks.protocol.official.common.BitDictBook;
import org.jetlinks.protocol.official.common.DictBook;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/18
 * @since V3.1.0
 */
public class YKCV1DictBookBuilder {

    /**
     * @return  网络链接类型
     */
    public static DictBook<Byte, String>    buildNetworkTypeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "SIM", "SIM");
        rst.add((byte) 0x00, "LAN", "LAN");
        rst.add((byte) 0x00, "WAN", "SIM");

        rst.addOtherItemTemplate((srcCode) -> "OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * @return  运营商
     */
    public static DictBook<Byte, String>    buildNetworkSPDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "CCM", "移动");
        rst.add((byte) 0x00, "CT", "电信");
        rst.add((byte) 0x00, "UC", "联通");

        rst.addOtherItemTemplate((srcCode) -> "OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * @return  登陆结果
     */
    public static DictBook<Byte, String>    buildLoginAuthRstCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "SUCCESS", "登陆成功");
        rst.add((byte) 0x00, "FAIL", "登陆失败");

        rst.addOtherItemTemplate((srcCode) -> "FAIL_OTHER_" + srcCode.toString(), "登陆失败：其他");

        return rst;
    }

    /**
     * @return  充电枪状态
     */
    public static DictBook<Byte, String>    buildGunStatusDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "OK", "正常");
        rst.add((byte) 0x00, "FAULT", "故障");

        rst.addOtherItemTemplate((srcCode) -> "FAULT_OTHER_" + srcCode.toString(), "登陆失败：其他");

        return rst;
    }

    /**
     * @return  充电桩状态
     */
    public static DictBook<Byte, String>    buildPileStatusDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "OFFLINE", "离线");
        rst.add((byte) 0x01, "FAULT", "故障");
        rst.add((byte) 0x02, "IDLE", "空闲");
        rst.add((byte) 0x02, "CHARGING", "充电");

        rst.addOtherItemTemplate((srcCode) -> "FAULT_OTHER_" + srcCode.toString(), "登陆失败：其他");

        return rst;
    }

    /**
     * @return  是/否/未知
     */
    public static DictBook<Byte, String>    buildYesOrNoDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "NO", "否");
        rst.add((byte) 0x01, "YES", "是");

        rst.addOtherItemTemplate((srcCode) -> "UKN_" + srcCode.toString(), "未知");

        return rst;
    }

    /**
     * 硬件故障字典表
     */
    public static BitDictBook<String>       buildFaultCodeDict() {
        BitDictBook<String> rst = new BitDictBook<>();

        rst.add("FC_EMG_STOP",      (short)1, (byte)0b00000001, "急停按钮动作故障");
        rst.add("FC_NO_REC_MD",     (short)1, (byte)0b00000010, "无可用整流模块");
        rst.add("FC_AIR_OUT_HT",    (short)1, (byte)0b00000100, "出风口温度过高");
        rst.add("FC_AC_LP",         (short)1, (byte)0b00001000, "交流防雷故障");
        rst.add("FC_DC20_NET",      (short)1, (byte)0b00010000, "交直流模块 DC20 通信中断");
        rst.add("FC_FC08_NET",      (short)1, (byte)0b00100000, "绝缘检测模块 FC08 通信中断");
        rst.add("FC_METER_NET",     (short)1, (byte)0b01000000, "电度表通信中断");
        rst.add("FC_READER_NET",    (short)1, (byte)0b10000000, "读卡器通信中断");
        rst.add("FC_RC10_NET",      (short)0, (byte)0b00000001, "RC10 通信中断");
        rst.add("FC_FAN_REG",       (short)0, (byte)0b00000010, "风扇调速板故障");
        rst.add("FC_DC_FUSE",       (short)0, (byte)0b00000100, "直流熔断器故障");
        rst.add("FC_HV_CT",         (short)0, (byte)0b00001000, "高压接触器故障");
        rst.add("FC_DOOR_OPEN",     (short)0, (byte)0b00010000, "门打开");

        return rst;
    }

    /**
     * BMS电池类型
     */
    public static DictBook<Byte, String>    buildBMSBatteryTypeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "铅酸电池", "铅酸电池");
        rst.add((byte) 0x02, "氢电池", "氢电池");
        rst.add((byte) 0x03, "磷酸铁锂电池", "磷酸铁锂电池");
        rst.add((byte) 0x04, "锰酸锂电池", "锰酸锂电池");
        rst.add((byte) 0x05, "钴酸锂电池", "钴酸锂电池");
        rst.add((byte) 0x06, "三元材料电池", "三元材料电池");
        rst.add((byte) 0x07, "聚合物锂离子电池", "聚合物锂离子电池");
        rst.add((byte) 0x08, "钛酸锂电池", "钛酸锂电池");

        rst.addOtherItemTemplate((srcCode) -> "OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * BMS 电池组产权标识
     */
    public static DictBook<Byte, String>    buildBMSBatteryOwnershipDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "租赁", "租赁");
        rst.add((byte) 0x01, "车自有", "车自有");

        rst.addOtherItemTemplate((srcCode) -> "OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 错误报文错误码
     */
    public static BitDictBook<String>       buildErrorReportErrorCodeDict() {
        BitDictBook<String> rst = new BitDictBook<>();

        rst.add("EC_SPN2560_00",    (short)0, (byte)0b11000000, (byte)0b00000000, "接收SPN2560=0x00的充电机辨识报文[正常]");
        rst.add("OK_SPN2560_00",    (short)0, (byte)0b11000000, (byte)0b01000000, "接收SPN2560=0x00的充电机辨识报文[超时]");
        rst.add("UNK_SPN2560_00",   (short)0, (byte)0b11000000, (byte)0b10000000, "接收SPN2560=0x00的充电机辨识报文[未知]");

        rst.add("EC_SPN2560_AA",    (short)0, (byte)0b00110000, (byte)0b00000000, "接收SPN2560=0xAA的充电机辨识报文[正常]");
        rst.add("OK_SPN2560_AA",    (short)0, (byte)0b00110000, (byte)0b00010000, "接收SPN2560=0xAA的充电机辨识报文[超时]");
        rst.add("UNK_SPN2560_AA",   (short)0, (byte)0b00110000, (byte)0b00100000, "接收SPN2560=0xAA的充电机辨识报文[未知]");


        rst.add("EC_CHG_MAX",    (short)1, (byte)0b11000000, (byte)0b00000000, "接收充电机的时间同步和充电机最大输出能力报文[正常]");
        rst.add("OK_CHG_MAX",    (short)1, (byte)0b11000000, (byte)0b01000000, "接收充电机的时间同步和充电机最大输出能力报文[超时]");
        rst.add("UNK_CHG_MAX",   (short)1, (byte)0b11000000, (byte)0b10000000, "接收充电机的时间同步和充电机最大输出能力报文[未知]");

        rst.add("EC_CHG_INIT",    (short)1, (byte)0b00110000, (byte)0b00000000, "接收充电机完成充电准备报文[正常]");
        rst.add("OK_CHG_INIT",    (short)1, (byte)0b00110000, (byte)0b00010000, "接收充电机完成充电准备报文[超时]");
        rst.add("UNK_CHG_INIT",   (short)1, (byte)0b00110000, (byte)0b00100000, "接收充电机完成充电准备报文[未知]");


        rst.add("EC_CHG_STU",    (short)2, (byte)0b11000000, (byte)0b00000000, "接收充电机充电状态报文[正常]");
        rst.add("OK_CHG_STU",    (short)2, (byte)0b11000000, (byte)0b01000000, "接收充电机充电状态报文[超时]");
        rst.add("UNK_CHG_STU",   (short)2, (byte)0b11000000, (byte)0b10000000, "接收充电机充电状态报文[未知]");

        rst.add("EC_CHG_END",    (short)2, (byte)0b00110000, (byte)0b00000000, "接收充电机中止充电报文[正常]");
        rst.add("OK_CHG_END",    (short)2, (byte)0b00110000, (byte)0b00010000, "接收充电机中止充电报文[超时]");
        rst.add("UNK_CHG_END",   (short)2, (byte)0b00110000, (byte)0b00100000, "接收充电机中止充电报文[未知]");


        rst.add("EC_CHG_STS",    (short)3, (byte)0b11000000, (byte)0b00000000, "接收充电机充电统计报文[正常]");
        rst.add("OK_CHG_STS",    (short)3, (byte)0b11000000, (byte)0b01000000, "接收充电机充电统计报文[超时]");
        rst.add("UNK_CHG_STS",   (short)3, (byte)0b11000000, (byte)0b10000000, "接收充电机充电统计报文[未知]");


        rst.add("EC_BMS_VIN",    (short)4, (byte)0b11000000, (byte)0b00000000, "接收BMS和车辆的辨识报文[正常]");
        rst.add("OK_BMS_VIN",    (short)4, (byte)0b11000000, (byte)0b01000000, "接收BMS和车辆的辨识报文[超时]");
        rst.add("UNK_BMS_VIN",   (short)4, (byte)0b11000000, (byte)0b10000000, "接收BMS和车辆的辨识报文[未知]");


        rst.add("EC_BMS_BAT_CONF",    (short)5, (byte)0b11000000, (byte)0b00000000, "接收电池充电参数报文[正常]");
        rst.add("OK_BMS_BAT_CONF",    (short)5, (byte)0b11000000, (byte)0b01000000, "接收电池充电参数报文[超时]");
        rst.add("UNK_BMS_BAT_CONF",   (short)5, (byte)0b11000000, (byte)0b10000000, "接收电池充电参数报文[未知]");

        rst.add("EC_BMS_CHR_INIT",    (short)5, (byte)0b00110000, (byte)0b00000000, "接收BMS完成充电准备报文[正常]");
        rst.add("OK_BMS_BAT_INIT",    (short)5, (byte)0b00110000, (byte)0b00010000, "接收BMS完成充电准备报文[超时]");
        rst.add("UNK_BMS_BAT_INIT",   (short)5, (byte)0b00110000, (byte)0b00100000, "接收BMS完成充电准备报文[未知]");


        rst.add("EC_BMS_BAT_STU",    (short)6, (byte)0b11000000, (byte)0b00000000, "接收电池充电总状态报文[正常]");
        rst.add("OK_BMS_BAT_STU",    (short)6, (byte)0b11000000, (byte)0b01000000, "接收电池充电总状态报文[超时]");
        rst.add("UNK_BMS_BAT_STU",   (short)6, (byte)0b11000000, (byte)0b10000000, "接收电池充电总状态报文[未知]");

        rst.add("EC_BMS_BAT_REQ",    (short)6, (byte)0b00110000, (byte)0b00000000, "接收电池充电要求报文[正常]");
        rst.add("OK_BMS_BAT_REQ",    (short)6, (byte)0b00110000, (byte)0b00010000, "接收电池充电要求报文[超时]");
        rst.add("UNK_BMS_BAT_REQ",   (short)6, (byte)0b00110000, (byte)0b00100000, "接收电池充电要求报文[未知]");

        rst.add("EC_BMS_TER_CHR",    (short)6, (byte)0b00001100, (byte)0b00000000, "接收BMS中止充电报文[正常]");
        rst.add("OK_BMS_TER_CHR",    (short)6, (byte)0b00001100, (byte)0b00000100, "接收BMS中止充电报文[超时]");
        rst.add("UNK_BMS_TER_CHR",   (short)6, (byte)0b00001100, (byte)0b00001000, "接收BMS中止充电报文[未知]");


        rst.add("EC_BMS_CHR_STS",    (short)7, (byte)0b11000000, (byte)0b00000000, "接收BMS充电统计报文[正常]");
        rst.add("OK_BMS_CHR_STS",    (short)7, (byte)0b11000000, (byte)0b01000000, "接收BMS充电统计报文[超时]");
        rst.add("UNK_BMS_CHR_STS",   (short)7, (byte)0b11000000, (byte)0b10000000, "接收BMS充电统计报文[未知]");

        return rst;
    }

    /**
     * BMS中止充电原因
     */
    public static BitDictBook<String>       buildBMSStopChargingReasonCodeDict() {
        BitDictBook<String> rst = new BitDictBook<>();

        rst.add("RC_FF_SOC",        (short)0, (byte)0b00000011, "达到SOC的目标值");
        rst.add("RC_FF_TV",         (short)0, (byte)0b00001100, "达到总电压的设定值");
        rst.add("RC_FF_SV",         (short)0, (byte)0b00110000, "达到单体电压设定值");
        rst.add("RC_CHG_STOP",      (short)0, (byte)0b11000000, "充电机主动中止");

        rst.add("FC_OTH",           (short)1, (byte)0b11000000, "其他故障");
        rst.add("FC_CHK2_VC",       (short)1, (byte)0b00110000, "检测点2电压检测故障");
        rst.add("FC_HV_RELAY",      (short)1, (byte)0b00001100, "高压继电器故障");
        rst.add("FC_BAT_OVT",       (short)1, (byte)0b00000011, "电池组温度过高故障");

        rst.add("FC_CHG_REPLY",     (short)2, (byte)0b11000000, "充电连接器故障");
        rst.add("FC_BMS_OVT",       (short)2, (byte)0b00110000, "BMS元件、输出连接器过温");
        rst.add("FC_OUT_OVT",       (short)2, (byte)0b00001100, "输出连接器过温故障");
        rst.add("FC_INSULATION",    (short)2, (byte)0b00000011, "绝缘故障");

        rst.add("EC_OVER_CUR",      (short)3, (byte)0b00000011, "电流过大");
        rst.add("EC_ABN_VOL",       (short)3, (byte)0b00001100, "电压异常");

        return rst;
    }

    /**
     * 充电机中止充电原因
     */
    public static BitDictBook<String>       buildChargerStopChargingReasonCodeDict() {
        BitDictBook<String> rst = new BitDictBook<>();

        rst.add("RC_FF_SETTING",    (short)0, (byte)0b11000000, "达到充电机设定的条件中止");
        rst.add("RC_MANUAL",        (short)0, (byte)0b00001100, "人工中止");
        rst.add("RC_ABN",           (short)0, (byte)0b00110000, "异常中止");
        rst.add("RC_BMS_STOP",      (short)0, (byte)0b11000000, "充电机主动中止");

        rst.add("FC_OTH",           (short)1, (byte)0b00001100, "其他故障");
        rst.add("FC_EMB_STOP",      (short)1, (byte)0b00000011, "充电机急停故障");

        rst.add("FC_EC_OVER",       (short)2, (byte)0b11000000, "所需电量不能传送");
        rst.add("FC_CHG_IN_OVT",    (short)2, (byte)0b00110000, "充电机内部过温");
        rst.add("FC_CHG_REPLY",     (short)2, (byte)0b00001100, "充电连接器故障");
        rst.add("FC_CHG_OVT",       (short)2, (byte)0b00000011, "充电机过温故障");

        rst.add("EC_CUR_NOT_FIT",   (short)3, (byte)0b00000011, "电流不匹配");
        rst.add("EC_ABN_VOL",       (short)3, (byte)0b00001100, "电压异常");

        return rst;
    }

    /**
     * 充电过程BMS状态码
     */
    public static BitDictBook<String>       buildBMSOnChargingStatusCodeDict() {
        BitDictBook<String> rst = new BitDictBook<>();

        rst.add("OK_BMS_S_BAT_VOL",     (short)0, (byte)0b11000000, (byte)0b00000000, "BMS单体动力蓄电池电压[正常]");
        rst.add("OH_BMS_S_BAT_VOL",     (short)0, (byte)0b11000000, (byte)0b01000000, "BMS单体动力蓄电池电压[过高]");
        rst.add("OL_BMS_S_BAT_VOL",     (short)0, (byte)0b11000000, (byte)0b10000000, "BMS单体动力蓄电池电压[过低]");

        rst.add("OK_BMS_T_BAT_SOC",     (short)0, (byte)0b00110000, (byte)0b00000000, "BMS整车动力蓄电池荷电状态SOC[正常]");
        rst.add("OH_BMS_T_BAT_SOC",     (short)0, (byte)0b00110000, (byte)0b00010000, "BMS整车动力蓄电池荷电状态SOC[过高]");
        rst.add("OL_BMS_T_BAT_SOC",     (short)0, (byte)0b00110000, (byte)0b00100000, "BMS整车动力蓄电池荷电状态SOC[过低]");

        rst.add("OK_BMS_T_BAT_CUR",     (short)0, (byte)0b00001100, (byte)0b00000000, "BMS动力蓄电池充电电流[正常]");
        rst.add("OV_BMS_T_BAT_CUR",     (short)0, (byte)0b00001100, (byte)0b00000100, "BMS动力蓄电池充电电流[过流]");
        rst.add("UKN_BMS_T_BAT_CUR",    (short)0, (byte)0b00001100, (byte)0b00001000, "BMS动力蓄电池充电电流[未知]");

        rst.add("OK_BMS_T_BAT_TMP",     (short)0, (byte)0b00000011, (byte)0b00000000, "BMS动力蓄电池温度[正常]");
        rst.add("OH_BMS_T_BAT_TMP",     (short)0, (byte)0b00000011, (byte)0b00000001, "BMS动力蓄电池温度[过高]");
        rst.add("OL_BMS_T_BAT_TMP",     (short)0, (byte)0b00000011, (byte)0b00000010, "BMS动力蓄电池温度[未知]");


        rst.add("OK_BMS_T_BAT_INS",     (short)1, (byte)0b11000000, (byte)0b00000000, "BMS动力蓄电池绝缘状态[正常]");
        rst.add("OV_BMS_T_BAT_INS",     (short)1, (byte)0b11000000, (byte)0b01000000, "BMS动力蓄电池绝缘状态[过流]");
        rst.add("UNK_BMS_T_BAT_INS",    (short)1, (byte)0b11000000, (byte)0b10000000, "BMS动力蓄电池绝缘状态[未知]");

        rst.add("OK_BMS_T_BAT_CNN",     (short)1, (byte)0b00110000, (byte)0b00000000, "BMS动力蓄电池组输出连接器连接状态[正常]");
        rst.add("OV_BMS_T_BAT_CNN",     (short)1, (byte)0b00110000, (byte)0b00010000, "BMS动力蓄电池组输出连接器连接状态[过流]");
        rst.add("UNK_BMS_T_BAT_CNN",    (short)1, (byte)0b00110000, (byte)0b00100000, "BMS动力蓄电池组输出连接器连接状态[未知]");

        rst.add("Y_CHG_FRB",            (short)1, (byte)0b00001100, (byte)0b00000000, "充电禁止[是]");
        rst.add("N_CHG_FRB",            (short)1, (byte)0b00001100, (byte)0b00000100, "充电禁止[否]");

        return rst;
    }

    /**
     * 运营平台确认启动充电失败原因
     */
    public static DictBook<Byte, String>    buildConfirmChargingFailReasonCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "FC_ACC_NOT_EXIST", "账户不存在");
        rst.add((byte) 0x01, "FC_ACC_FROZE", "账户冻结");
        rst.add((byte) 0x01, "FC_ACC_BALANCE", "账户余额不足");
        rst.add((byte) 0x01, "FC_ACC_NOT_PAY", "该卡存在未结账记录");
        rst.add((byte) 0x01, "FC_PILE_FRB", "桩停用");
        rst.add((byte) 0x01, "FC_ACC_FRB_ON_PILE", "该账户不能在此桩上充电");
        rst.add((byte) 0x01, "FC_PSW_ERR", "密码错误");
        rst.add((byte) 0x01, "FC_CAP_NOT_ENOUGH", "电站电容不足");
        rst.add((byte) 0x01, "FC_VIN_NOT_EXIST", "系统中VIN码不存在");
        rst.add((byte) 0x01, "FC_PILE_NOT_PAY", "该桩存在未结账记录");
        rst.add((byte) 0x01, "FC_PILE_NOT_SUP", "该桩不支持刷卡");

        rst.addOtherItemTemplate((srcCode) -> "OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 成功或失败
     */
    public static DictBook<Byte, String>    buildSuccessOrFailDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "SUCCESS", "成功", true);
        rst.add((byte) 0x00, "FAIL", "失败");

        rst.addOtherItemTemplate((srcCode) -> "FAIL_OTH_" + srcCode.toString(), "其他原因失败");

        return rst;
    }

    /**
     * 远程启动充电命令回复失败原因
     */
    public static DictBook<Byte, String>    buildRemoteSwitchOnFailReasonCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "FC_VOID", "无");
        rst.add((byte) 0x01, "FC_WRONG_NO", "设备编号不匹配");
        rst.add((byte) 0x01, "FC_GUN_IN_CHG", "枪已在充电");
        rst.add((byte) 0x01, "FC_DEVICE_FAULT", "设备故障");
        rst.add((byte) 0x01, "FC_DEVICE_OFFLINE", "设备离线");
        rst.add((byte) 0x01, "FC_GUN_NOT_PLUGIN", "未插枪");

        rst.addOtherItemTemplate((srcCode) -> "FC_OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 远程启动充电命令回复失败原因
     */
    public static DictBook<Byte, String>    buildRemoteSwitchOffFailReasonCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "FC_VOID", "无");
        rst.add((byte) 0x01, "FC_WRONG_NO", "设备编号不匹配");
        rst.add((byte) 0x02, "FC_GUN_NOT_IN_CHG", "枪未处于充电状态");

        rst.addOtherItemTemplate((srcCode) -> "FC_OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 交易标识
     */
    public static DictBook<Byte, String>    buildTranFlagDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "TRANS_INIT_BY_APP", "APP启动");
        rst.add((byte) 0x02, "TRANS_INIT_BY_ID_CARD", "卡启动");
        rst.add((byte) 0x03, "TRANS_INIT_BY_IC_CARD", "离线卡启动");
        rst.add((byte) 0x05, "TRANS_INIT_BY_VIN", "VIN码启动充电");

        rst.addOtherItemTemplate((srcCode) -> "FC_OTH_" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 余额更新修改结果
     */
    public static DictBook<Byte, String>    buildBalanceUpdateResultDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "SUCCESS", "修改成功", true);
        rst.add((byte) 0x01, "FAIL_WRONG_DEVICE", "设备编号错误");
        rst.add((byte) 0x02, "FAIL_WRONG_CARD", "卡号错误");

        rst.addOtherItemTemplate((srcCode) -> "FAIL_OTH_" + srcCode.toString(), "其他错误");

        return rst;
    }

    /**
     * 离线卡数据下发/更新/清除结果
     */
    public static DictBook<Byte, String>    buildICCardUpdateResultDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "FAIL_WRONG_CARD_NO", "卡号格式错误");
        rst.add((byte) 0x02, "FAIL_NOT_ENOUGH_SPACE", "储存空间不足");

        rst.addOtherItemTemplate((srcCode) -> "FAIL_OTH_" + srcCode.toString(), "其他错误");

        return rst;
    }

    /**
     * 车位锁状态
     */
    public static DictBook<Byte, String>    buildParkLockStatusCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "SC_LOCK_NOT_HOME", "未到位状态");
        rst.add((byte) 0x55, "SC_LOCK_TO_TG", "升锁到位状态");
        rst.add((byte) 0xFF, "SC_UNLOCK_TO_TG", "降锁到位状态");

        rst.addOtherItemTemplate((srcCode) -> "SC_OTH" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 车位锁报警状态
     */
    public static DictBook<Byte, String>    buildParkLockAlarmCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "AC_OK", "正常无报警");
        rst.add((byte) 0xFF, "AC_BROKEN", "待机状态摇臂破坏");
        rst.add((byte) 0x55, "AC_ABN", "摇臂升降异常(未到位)");

        rst.addOtherItemTemplate((srcCode) -> "AC_OTH" + srcCode.toString(), "其他");

        return rst;
    }

    /**
     * 桩型号
     */
    public static DictBook<Byte, String>    buildPileModelDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "PILE_DC", "直流充电桩");
        rst.add((byte) 0x01, "PILE_AC", "交流充电桩");

        return rst;
    }

    /**
     * 执行控制
     */
    public static DictBook<Byte, String>    buildCmdImmediateDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x01, "RUN_AT_ONCE", "立即执行");
        rst.add((byte) 0x02, "RUN_IN_IDLE", "空闲执行");

        return rst;
    }

    /**
     * 远程更新升级状态
     */
    public static DictBook<Byte, String>    buildOTAResultCodeDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "SUCCESS", "成功");
        rst.add((byte) 0x01, "FAIL_WRONG_NO", "编号错误");
        rst.add((byte) 0x02, "FAIL_NOT_FIT", "程序与桩型号不符");
        rst.add((byte) 0x03, "FAIL_TIMEOUT", "下载更新文件超时");

        return rst;
    }



}
