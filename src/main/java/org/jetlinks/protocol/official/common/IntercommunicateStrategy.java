package org.jetlinks.protocol.official.common;

import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.message.DeviceOnlineMessage;

/**
 * 通讯交互策略：
 * <li>不包含显式的设备认证消息，只有特定类型的消息才会包含设备信息</li>
 * <li>特定类型的消息包含设备认证的信息（为了描述方便称呼为L类型消息）,可约定该类消息触发设备认证或设备上线</li>
 * <li>L类型消息：可能会周期性上报，TCP链接建立后首次消息触发设备认证和设备上线，设备认证成功后L类型消息保持语义返回</li>
 * <li>设备认证前的非L类型的消息将会被丢弃，只通过日志留痕</li>
 */
public interface IntercommunicateStrategy {

    boolean     canFireLogin(DeviceMessage msg);

    DeviceOnlineMessage buildLoginMessage(DeviceMessage sourceMsg);

    boolean     needAckWhileLoginSuccess();

    boolean     needAckWhileLoginFail();

    boolean     needCloseConnectionWhileSendAckFail();

    boolean     isAckWhileIgnored();

    boolean     ackWhileReceived();

}
