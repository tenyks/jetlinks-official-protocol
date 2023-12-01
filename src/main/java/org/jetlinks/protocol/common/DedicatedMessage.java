package org.jetlinks.protocol.common;

import java.util.Map;

/**
 * 专用协议消息，在传输层协议之上，底座协议之下
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2023/11/30
 * @since V1.3.0
 */
public interface DedicatedMessage {

    void readFields(String topic, Map<String, Object> buf);

}
