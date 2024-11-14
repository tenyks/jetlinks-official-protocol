package me.tenyks.qianye;

import org.jetlinks.protocol.common.mapping.ThingValueNormalization;
import org.jetlinks.protocol.common.mapping.ThingValueNormalizations;
import org.jetlinks.protocol.official.common.DictBook;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/18
 * @since V3.1.0
 */
public class WuHanQianYeV1DictBookBuilder {

    /**
     * @return  读属性的枚举
     */
    public static ThingValueNormalization<String> buildReadPropertyDict() {
        DictBook<String, String> rst = new DictBook<>();

        rst.add("ALL", "All", "全部状况（需设备支持）");
        rst.add("RELAY", "Relay", "通断状况");
        rst.add("POWER", "Power", "电压、电流以及功率状况");

        return ThingValueNormalizations.ofToDictVal(rst, "ALL");
    }

    /**
     * @return  通断选项的枚举
     */
    public static ThingValueNormalization<Boolean> buildSocketSwitchOptionDict() {
        DictBook<String, Boolean> rst = new DictBook<>();

        rst.add("ON", Boolean.TRUE, "通电");
        rst.add("1", Boolean.TRUE, "通电");

        rst.add("OFF", Boolean.FALSE, "断电");
        rst.add("0", Boolean.FALSE, "断电");

        return ThingValueNormalizations.ofToDictVal(rst, Boolean.FALSE);
    }

    /**
     * @return  通断状态的枚举
     */
    public static ThingValueNormalization<String> buildRelayStatusDict() {
        DictBook<String, String> rst = new DictBook<>();

        rst.add("1", "ON", "通电");
        rst.add("true", "ON", "通电");
        rst.add("TRUE", "ON", "通电");

        rst.add("0", "OFF", "断电");
        rst.add("false", "OFF", "断电");
        rst.add("FALSE", "OFF", "断电");

        return ThingValueNormalizations.ofToDictVal(rst, "UNK");
    }

    public static ThingValueNormalization<String> buildSocketStatusDict() {
        DictBook<Integer, String> rst = new DictBook<>();

        rst.add(1, "ON", "通电");
        rst.add(0, "OFF", "断电");

        return ThingValueNormalizations.ofToDictVal(rst, null);
    }

    public static ThingValueNormalization<String> buildRstCodeDict() {
        DictBook<Integer, String> rst = new DictBook<>();

        rst.add(200, "SUCCESS", "成功");

        rst.add(0, "FAIL_OFFLINE", "失败：设备离线");
        rst.add(6, "FAIL_UNACTIVATED", "失败：未激活");
        rst.add(7, "FAIL_DISABLE", "失败：禁用");
        rst.add(21, "FAIL_DESERIALIZE", "失败：序列化错误");
        rst.add(22, "FAIL_FIELD_NOT_EXIST", "失败：缺少字段");
        rst.add(23, "FAIL_FORMAT", "失败：格式错误");

        rst.addOtherItemTemplate((code) -> "FAIL_" + code, "失败：未知");

        return ThingValueNormalizations.ofToDictVal(rst, null);
    }

    public static ThingValueNormalization<String> buildActiveModeDict() {
        DictBook<Integer, String> rst = new DictBook<>();

        //TCP_SERVER=TCP服务器，TCP_CLIENT=TCP客户端, HTTP_SERVER=HTTP服务器， HTTP_CLIENT=HTTP客户端，UDP=UDP模式，FACTORY=工厂模式，MQTT=MQTT模式

        rst.add(1, "TCP_CLIENT", "TCP客户端");
        rst.add(2, "TCP_SERVER", "TCP服务器");
        rst.add(3, "HTTP_SERVER", "HTTP服务器");
        rst.add(4, "HTTP_CLIENT", "HTTP客户端");
        rst.add(5, "UDP", "UDP");
        rst.add(6, "FACTORY", "工程模式");
        rst.add(7, "MQTT", "MQTT模式");

        return ThingValueNormalizations.ofToDictVal(rst, "UNK");
    }
}
