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
        rst.add("REPLY", "Reply", "通断状况");
        rst.add("POWER", "Power", "电压、电流以及功率状况");

        return ThingValueNormalizations.ofToDictVal(rst, "All");
    }

    /**
     * @return  通断状态的枚举
     */
    public static ThingValueNormalization<String> buildReplyStatusDict() {
        DictBook<String, String> rst = new DictBook<>();

        rst.add("1", "ON", "通电");
        rst.add("true", "ON", "通电");
        rst.add("TRUE", "ON", "通电");

        rst.add("0", "OFF", "断电");
        rst.add("false", "OFF", "断电");
        rst.add("FALSE", "OFF", "断电");

        return ThingValueNormalizations.ofToDictVal(rst, "UNK");
    }

}
