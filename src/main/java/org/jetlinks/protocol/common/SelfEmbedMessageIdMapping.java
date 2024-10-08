package org.jetlinks.protocol.common;

/**
 * 自包含的消息ID映射，适用于：短展开为长的情况
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/8
 * @since V3.1.0
 */
public class SelfEmbedMessageIdMapping implements MessageIdMapping {

    @Override
    public boolean submitBinding(String thingMsgId, String protocolMsgId) {
        return false;
    }

    @Override
    public String revokeBinding(String protocolMsgId) {
        return null;
    }

}
