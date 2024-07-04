package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

/**
 * 物模型消息ID与协议消息ID映射标注
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/4
 * @since V3.1.0
 */
public interface MessageIdMappingAnnotation {

    void    mark(@NotNull String thingMsgId, @NotNull String sessionId, @NotNull StructInstance structInst);

    String  take(@NotNull String sessionId, @NotNull StructInstance structInst);

}
