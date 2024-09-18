package me.tenyks.qiyun.protocol;

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
     * @return  是/否/未知
     */
    public static DictBook<Byte, String>    buildYesOrNoDict() {
        DictBook<Byte, String> rst = new DictBook<>();

        rst.add((byte) 0x00, "NO", "否");
        rst.add((byte) 0x01, "YES", "是");

        rst.addOtherItemTemplate((srcCode) -> "UKN_" + srcCode.toString(), "未知");

        return rst;
    }

}
