package org.jetlinks.protocol.common;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/8
 * @since V3.1.0
 */
public interface MessageIdReverseMapping<T> {

    /**
     * 提交物模型消息ID与协议消息ID的绑定关系
     * @param thingMsgId        物模型消息ID,（必要）
     * @param protocolMsgId     协议消息ID,（必要）
     * @return  如果已存在绑定关系，返回false, 否则返回true
     */
    boolean     submitBinding(String thingMsgId, T protocolMsgId);

    /**
     * 核销物模型消息ID与协议消息ID的绑定关系，并返回物模型消息ID
     * @param thingMsgId     协议消息ID,（必要）
     * @return  如果存在绑定关系返回物模型消息ID，否则返回空
     */
    T           revokeBinding(String thingMsgId);

}
