package org.jetlinks.protocol.official.common;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.event.EventMessage;
import org.jetlinks.protocol.common.DeviceRequestHandler;
import org.jetlinks.protocol.common.UplinkMessageReplyResponder;

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
        EventMessage eSrcMsg = (EventMessage) sourceMsg;

        DeviceOnlineMessage dstMsg = new DeviceOnlineMessage();
        dstMsg.setDeviceId(eSrcMsg.getDeviceId());
        dstMsg.setMessageId(eSrcMsg.getMessageId());
        dstMsg.setTimestamp(eSrcMsg.getTimestamp());

        return dstMsg;
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
