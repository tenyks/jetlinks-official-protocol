package org.jetlinks.protocol.common;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/1
 * @since V3.1.0
 */
public class SimpleUplinkMessageReplyResponder implements UplinkMessageReplyResponder {

    private static final Logger log = LoggerFactory.getLogger(SimpleUplinkMessageReplyResponder.class);

    private final Map<String, Function<DeviceMessage, JSONObject>> index;

    public SimpleUplinkMessageReplyResponder() {
        this.index = new HashMap<>();
    }

    @Override
    public boolean hasAckForMessage(@NotNull DeviceMessage uplinkMsg) {
        return false;
    }

    @Override
    public AcknowledgeDeviceMessage buildAckMessage(@NotNull DeviceOperator device, @NotNull DeviceMessage uplinkMsg) {
        String svcId = uplinkMsg.getIdentity();

        if (svcId == null) return null;

        Function<DeviceMessage, JSONObject> callable = index.get(svcId);
        if (callable == null) return null;

        AcknowledgeDeviceMessage ackMsg = new AcknowledgeDeviceMessage().from(uplinkMsg);

        JSONObject payload = callable.apply(uplinkMsg);
        ackMsg.setOutput(payload);

        return ackMsg;
    }

    public void addMappingAndReply(StructDeclaration src, Function<DeviceMessage, JSONObject> ackPayloadFun) {
        String svcIdOrFunctionId = src.getServiceIdOrFunctionId();

        if (svcIdOrFunctionId == null) {
            log.warn("StructDeclaration缺少serviceId或functionId标注");
            return ;
        }

        index.put(svcIdOrFunctionId, ackPayloadFun);
    }

}
