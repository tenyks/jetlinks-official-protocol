package org.jetlinks.protocol.dataSky;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.event.ThingEventMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.protocol.common.DedicatedMessageDecoder;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2023/11/30 23:12
 */
public class DataSkyDedicatedMessageDecoder implements DedicatedMessageDecoder {

    @Nonnull
    @Override
    public Publisher<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload) {


        return null;
    }

    private ReportPropertyMessage       buildReportPropertyMessage(DataSkyWiFiProbeSubmitVo submitVo) {
        


        return null;
    }

    private List<ThingEventMessage>     readReportSamples(List<DataSkyWiFiSampleVo> samples) {
        return null;
    }

    private DataSkyWiFiProbeSubmitVo    decodePayload(ObjectMapper mapper, byte[] payload)
            throws JsonProcessingException {
        String jsonStr = new String(payload, StandardCharsets.UTF_8);

        return mapper.readValue(jsonStr, DataSkyWiFiProbeSubmitVo.class);
    }

}
