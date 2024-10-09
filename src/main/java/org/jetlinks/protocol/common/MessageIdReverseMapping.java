package org.jetlinks.protocol.common;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/8
 * @since V3.1.0
 */
public interface MessageIdReverseMapping<T> {

    /**
     *
     * @return  物模型消息ID
     */
    String      mark(T protocolMsgId);

    /**
     * @param thingMsgId     协议消息ID,（必要）
     * @return  协议消息ID
     */
    T           take(String thingMsgId);

}
