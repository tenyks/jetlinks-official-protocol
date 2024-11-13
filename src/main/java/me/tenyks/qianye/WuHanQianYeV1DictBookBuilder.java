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
