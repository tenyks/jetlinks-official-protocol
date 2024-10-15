package org.jetlinks.protocol.official.common;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.core.message.request.DefaultDeviceRequestMessage;
import org.jetlinks.protocol.common.DeviceRequestHandler;
import org.jetlinks.protocol.common.UplinkMessageReplyResponder;

import java.util.HashMap;

public abstract class AbstractIntercommunicateStrategy implements IntercommunicateStrategy {

    private DeviceRequestHandler        requestHandler;

    private UplinkMessageReplyResponder replyResponder;

    public AbstractIntercommunicateStrategy() {

    }

    public AbstractIntercommunicateStrategy(DeviceRequestHandler requestHandler, UplinkMessageReplyResponder replyResponder) {
        this.requestHandler = requestHandler;
        this.replyResponder = replyResponder;
    }

    @Override
    public DeviceRequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public UplinkMessageReplyResponder getReplyResponder() {
        return replyResponder;
    }

    public AbstractIntercommunicateStrategy setRequestHandler(DeviceRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    public AbstractIntercommunicateStrategy setReplyResponder(UplinkMessageReplyResponder replyResponder) {
        this.replyResponder = replyResponder;
        return this;
    }

    @Override
    public boolean canFireLogin(DeviceMessage msg) {
        return false;
    }

    @Override
    public DeviceOnlineMessage buildLoginMessage(DeviceMessage sourceMsg) {
        if (sourceMsg instanceof DefaultDeviceRequestMessage) {
            DefaultDeviceRequestMessage reqMsg = (DefaultDeviceRequestMessage) sourceMsg;

            DeviceOnlineMessage dstMsg = new DeviceOnlineMessage();
            dstMsg.setDeviceId(reqMsg.getDeviceId());
            dstMsg.setMessageId(reqMsg.getMessageId());
            dstMsg.setTimestamp(reqMsg.getTimestamp());
            dstMsg.setCode("SUCCESS");
            if (reqMsg.getInputs() != null) {
                if (dstMsg.getHeaders() == null) dstMsg.setHeaders(new HashMap<>());
                dstMsg.getHeaders().putAll(reqMsg.getInputs());
            }

            return dstMsg;
        } else {
            EventMessage eSrcMsg = (EventMessage) sourceMsg;

            DeviceOnlineMessage dstMsg = new DeviceOnlineMessage();
            dstMsg.setDeviceId(eSrcMsg.getDeviceId());
            dstMsg.setMessageId(eSrcMsg.getMessageId());
            dstMsg.setTimestamp(eSrcMsg.getTimestamp());

            if (eSrcMsg.getData() != null && eSrcMsg.getData() instanceof JSONObject) {
                if (dstMsg.getHeaders() == null) dstMsg.setHeaders(new HashMap<>());
                dstMsg.getHeaders().putAll((JSONObject)eSrcMsg.getData());
            }

            return dstMsg;
        }
    }

    @Override
    public boolean needAckWhileLoginSuccess() {
        return false;
    }

    @Override
    public boolean needAckWhileLoginFail() {
        return false;
    }

    @Override
    public boolean needCloseConnectionWhileSendAckFail() {
        return false;
    }

    @Override
    public boolean isAckWhileIgnored() {
        return false;
    }

    @Override
    public boolean ackWhileReceived() {
        return false;
    }
}
