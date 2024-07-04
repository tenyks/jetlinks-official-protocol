package org.jetlinks.protocol.common;

/**
 * 平台MessageId与特定协议消息ID的映射
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/4
 * @since V3.1.0
 */
public interface MessageIdMapping {

    /**
     * 提交物模型消息ID与协议消息ID的绑定关系
     * @param thingMsgId        物模型消息ID,（必要）
     * @param protocolMsgId     协议消息ID,（必要）
     * @return  如果已存在绑定关系，返回false, 否则返回true
     */
    boolean     submitBinding(String thingMsgId, String protocolMsgId);

    /**
     * 核销物模型消息ID与协议消息ID的绑定关系，并返回物模型消息ID
     * @param protocolMsgId     协议消息ID,（必要）
     * @return  如果存在绑定关系返回物模型消息ID，否则返回空
     */
    String      revokeBinding(String protocolMsgId);

}
