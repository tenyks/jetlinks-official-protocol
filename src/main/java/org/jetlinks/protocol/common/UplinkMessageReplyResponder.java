package org.jetlinks.protocol.common;

import org.jetlinks.core.device.DeviceOperator;
import org.jetlinks.core.message.AcknowledgeDeviceMessage;
import org.jetlinks.core.message.DeviceMessage;

import javax.validation.constraints.NotNull;

/**
 * （非设备请求消息）上行消息应答器
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/1
 * @since V3.1.0
 */
public interface UplinkMessageReplyResponder {

    /**
     * 判断指定的上行消息是否需要响应应答消息
     * @param uplinkMsg     上行消息，（必要）
     * @return  如果需要应当返回true，否则返回false
     */
    boolean                     hasAckForMessage(@NotNull DeviceMessage uplinkMsg);

    /**
     * 根据输入的上行消息构造应答消息
     * @param uplinkMsg         上行消息，（必要）
     * @return  如果需要响应返回匹配的应答消息，否则返回空
     */
    AcknowledgeDeviceMessage    buildAckMessage(@NotNull DeviceMessage uplinkMsg);

}
