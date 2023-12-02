package org.jetlinks.protocol.dataSky;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.tenyks.core.utils.ShortCodeGenerator;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.property.ReportPropertyMessage;
import org.jetlinks.protocol.common.DedicatedMessageDecoder;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * @author v-lizy81
 * @date 2023/11/30 23:12
 */
public class DataSkyDedicatedMessageDecoder implements DedicatedMessageDecoder {

    private static final ShortCodeGenerator MSG_ID_GENERATOR = new ShortCodeGenerator();

    private static final Logger log = LoggerFactory.getLogger(DataSkyDedicatedMessageDecoder.class);

    @Nonnull
    @Override
    public Publisher<DeviceMessage> decode(ObjectMapper mapper, String[] topics, byte[] payload) {
        final String deviceId = topics[1];
        final String msgIdPrefix = MSG_ID_GENERATOR.next();

        try {
            DataSkyWiFiProbeSubmitVo inputVo = decodePayload(mapper, payload);
            if (inputVo == null) return Mono.empty();

            List<DeviceMessage> msgList = new ArrayList<>();

            ReportPropertyMessage mainMsg = buildReportPropertyMessage(deviceId, msgIdPrefix, inputVo);
            msgList.add(mainMsg);
            msgList.addAll(readReportSamples(inputVo, inputVo.getData(), mainMsg.getDeviceId(), msgIdPrefix));

            return Flux.fromIterable(msgList);
        } catch (Exception e) {
            log.error("[DataSky]payload反序列失败：{}", Base64.getEncoder().encodeToString(payload));
            return Flux.empty();
        }
    }

    private ReportPropertyMessage       buildReportPropertyMessage(String deviceId, String msgIdPrefix, DataSkyWiFiProbeSubmitVo submitVo) {
        ReportPropertyMessage msg = DataSkyThingDefine.createReportPropertyMessage(deviceId, msgIdPrefix + "_00000");

        submitVo.readFields(DataSkyThingDefine.THING_ID_OF_REPORT_PROPERTIES, msg.getProperties());

        return msg;
    }

    private List<EventMessage>     readReportSamples(DataSkyWiFiProbeSubmitVo submitVo,
                                                     List<DataSkyWiFiSampleVo> samples,
                                                     String deviceId, String refMsgId) {
        if (samples == null || samples.isEmpty()) return Collections.emptyList();

        List<EventMessage> rst = new ArrayList<>(samples.size());

        int seqNo = 1;
        for (DataSkyWiFiSampleVo sampleVo : samples) {
            String msgId = String.format("%s_%05d", refMsgId, seqNo);

            Map<String, Object> msgData = new HashMap<>();
            EventMessage tem;

            if (sampleVo.isOfAPSample()) {
                sampleVo.readFields(DataSkyThingDefine.THING_ID_OF_REPORT_AP_SAMPLE, msgData);
                submitVo.readFields(DataSkyThingDefine.THING_ID_OF_REPORT_AP_SAMPLE, msgData);
                tem = DataSkyThingDefine.createReportAPSample(deviceId, msgId, msgData);
            } else {
                sampleVo.readFields(DataSkyThingDefine.THING_ID_OF_REPORT_CLIENT_SAMPLE, msgData);
                submitVo.readFields(DataSkyThingDefine.THING_ID_OF_REPORT_CLIENT_SAMPLE, msgData);
                tem = DataSkyThingDefine.createReportClientSample(deviceId, msgId, msgData);
            }

            rst.add(tem);
            seqNo++;
        }

        return rst;
    }

    private DataSkyWiFiProbeSubmitVo    decodePayload(ObjectMapper mapper, byte[] payload)
            throws JsonProcessingException {
        String jsonStr = new String(payload, StandardCharsets.UTF_8);

        return mapper.readValue(jsonStr, DataSkyWiFiProbeSubmitVo.class);
    }

}
