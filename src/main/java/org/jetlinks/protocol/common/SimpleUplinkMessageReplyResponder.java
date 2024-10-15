package org.jetlinks.protocol.common;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.protocol.official.binary.BinaryMessageType;
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
    private final Map<String, String> ackSvcIdIdx;

    public SimpleUplinkMessageReplyResponder() {
        this.index = new HashMap<>();
        this.ackSvcIdIdx = new HashMap<>();
    }

    @Override
    public boolean hasAckForMessage(@NotNull DeviceMessage uplinkMsg) {
        return false;
    }

    @Override
    public AcknowledgeDeviceMessage buildAckMessage(@NotNull DeviceMessage uplinkMsg) {
        String svcId = uplinkMsg.getServiceId();

        if (svcId == null) {
            log.warn("[ReplyResponder]缺少消息缺少serviceId或functionId：{}", uplinkMsg.toJson());
            return null;
        }

        Function<DeviceMessage, JSONObject> callable = index.get(svcId);
        if (callable == null) {
            log.warn("[ReplyResponder]缺少消息({}, {})的ACK设置(1)", uplinkMsg.getMessageId(), svcId);
            return null;
        }

        String ackFunId = ackSvcIdIdx.get(svcId);
        if (ackFunId == null) {
            log.warn("[ReplyResponder]缺少消息({}, {})的ACK设置(2)", uplinkMsg.getMessageId(), svcId);
            return null;
        }

        AcknowledgeDeviceMessage ackMsg = new AcknowledgeDeviceMessage().from(uplinkMsg);

        JSONObject payload = callable.apply(uplinkMsg);
        ackMsg.setOutputs(payload);
        ackMsg.setFunctionId(ackFunId);

        uplinkMsg.getHeader(BinaryMessageType.HEADER_MSG_SEQ)
                .ifPresent(seq -> ackMsg.addHeader(BinaryMessageType.HEADER_MSG_SEQ, seq));

        return ackMsg;
    }

    public void addMappingAndReply(StructDeclaration src, String ackServiceId, Function<DeviceMessage, JSONObject> ackPayloadFun) {
        String svcIdOrFunctionId = src.getServiceIdOrFunctionId();

        if (svcIdOrFunctionId == null) {
            log.warn("StructDeclaration缺少serviceId或functionId标注");
            return ;
        }

        index.put(svcIdOrFunctionId, ackPayloadFun);
        ackSvcIdIdx.put(svcIdOrFunctionId, ackServiceId);
    }

}
