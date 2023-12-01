package org.jetlinks.protocol.dataSky;

import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.core.things.ThingType;

import java.util.HashMap;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/12/1
 * @since V1.3.0
 */
public class DataSkyThingDefine {

    public static String THING_ID_OF_REPORT_PROPERTIES = "ReportProperties";

    public static String THING_ID_OF_REPORT_CLIENT_SAMPLE = "ReportAPSample";

    public static String THING_ID_OF_REPORT_AP_SAMPLE = "ReportClientSample";

    public static ReportPropertyMessage     createReportPropertyMessage() {
        ReportPropertyMessage rst = ReportPropertyMessage.create();
        rst.thingId(ThingType.of("DataSkyWIFIProbe"), THING_ID_OF_REPORT_PROPERTIES);
        rst.properties(new HashMap<>());

        return rst;
    }



}
