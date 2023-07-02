package org.jetlinks.protocol.official.common;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;
import org.jetlinks.core.message.event.EventMessage;

public abstract class AbstractIntercommunicateStrategy implements IntercommunicateStrategy {

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
