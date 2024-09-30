package me.tenyks.dataSky;

import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.things.ThingType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/12/1
 * @since V1.3.0
 */
public class DataSkyThingDefine {

    public static String THING_ID_OF_REPORT_PROPERTIES = "ReportProperties";

    public static String THING_ID_OF_REPORT_CLIENT_SAMPLE = "ReportClientSample";

    public static String THING_ID_OF_REPORT_AP_SAMPLE = "ReportAPSample";

    public static ReportPropertyMessage     createReportPropertyMessage(String deviceId, String msgId) {
        ReportPropertyMessage rst = ReportPropertyMessage.create();
        rst.thingId(ThingType.of("DataSkyWIFIProbe"), THING_ID_OF_REPORT_PROPERTIES);
        rst.properties(new HashMap<>());
        rst.setDeviceId(deviceId);
        rst.setMessageId(msgId);

        return rst;
    }

    public static EventMessage createReportAPSample(String deviceId, String msgId, Map<String, Object> data) {
        EventMessage tem = new EventMessage();
        tem.setDeviceId(deviceId);
        tem.event(THING_ID_OF_REPORT_AP_SAMPLE);
        tem.data(data);
        tem.messageId(msgId);

        return tem;
    }

    public static EventMessage createReportClientSample(String deviceId, String msgId, Map<String, Object> data) {
        EventMessage tem = new EventMessage();
        tem.setDeviceId(deviceId);
        tem.event(THING_ID_OF_REPORT_CLIENT_SAMPLE);
        tem.data(data);
        tem.messageId(msgId);

        return tem;
    }
}
