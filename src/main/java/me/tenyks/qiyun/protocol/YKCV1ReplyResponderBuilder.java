package me.tenyks.qiyun.protocol;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessage;
import org.jetlinks.protocol.common.SimpleUplinkMessageReplyResponder;
import org.jetlinks.protocol.common.UplinkMessageReplyResponder;
import org.jetlinks.protocol.official.binary.AckCode;
import org.jetlinks.protocol.official.binary2.StructSuit;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/1
 * @since V3.1.0
 */
public class YKCV1ReplyResponderBuilder {

    public UplinkMessageReplyResponder  build(StructSuit suit) {
        SimpleUplinkMessageReplyResponder rst = new SimpleUplinkMessageReplyResponder();

        rst.addMappingAndReply(suit.getStructDeclaration("充电桩登录认证消息[上行]"), "AuthResponse", this::buildAckOfLogin);
        rst.addMappingAndReply(suit.getStructDeclaration("充电桩心跳包[上行]"), "HeartBeatPong", this::buildAckOfHeartBeatPing);
        rst.addMappingAndReply(suit.getStructDeclaration("上报交易记录[上行]"), "ReportTransOrderAck", this::buildAckOfReportTransOrderAck);

        return rst;
    }

    private JSONObject buildAckOfLogin(DeviceMessage devMsg) {
        DefaultDeviceRequestMessage reqMsg = (DefaultDeviceRequestMessage) devMsg;

        String  pileNo = reqMsg.getInputStr("pileNo");
        AckCode ackCode = (AckCode) reqMsg.getHeaderOrElse("ACK", () -> AckCode.noAuth);

        JSONObject ackPayload = new JSONObject();
        ackPayload.put("pileNo", pileNo);
        //
        ackPayload.put("rstFlag", (ackCode != null && ackCode.equals(AckCode.ok) ? (byte) 0x00 : (byte) 0x01));

        return ackPayload;
    }

    private JSONObject buildAckOfHeartBeatPing(DeviceMessage devMsg) {
        EventMessage event = (EventMessage) devMsg;

        JSONObject data = (JSONObject)event.getData();
        String  pileNo = data.getString("pileNo");
        Byte gunNo = data.getByte("gunNo");

        JSONObject ackPayload = new JSONObject();
        ackPayload.put("pileNo", pileNo);
        ackPayload.put("gunNo", gunNo);
        ackPayload.put("pongFlag", (byte) 0);

        return ackPayload;
    }

    private JSONObject buildAckOfReportTransOrderAck(DeviceMessage devMsg) {
        EventMessage event = (EventMessage) devMsg;

        JSONObject data = (JSONObject)event.getData();
        String  transNo = data.getString("transNo");

        JSONObject ackPayload = new JSONObject();
        ackPayload.put("transNo", transNo);
        ackPayload.put("rstFlag", (byte) 0x00);

        return ackPayload;
    }

}
