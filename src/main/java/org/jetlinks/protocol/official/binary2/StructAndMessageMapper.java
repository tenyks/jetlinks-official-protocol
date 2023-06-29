package org.jetlinks.protocol.official.binary2;

import org.jetlinks.core.message.DeviceMessage;

/**
 * 结构体与物模型消息的映射器<br>
 * 负责：
 * <li>定义的映射：结构与模型消息的映射，结构字段与物模型属性的映射；</li>
 * <li>实例的映射：字段取值与属性取值的映射；</li>
 *
 * @author v-lizy81
 * @date 2023/6/12 23:14
 */
public interface StructAndMessageMapper {

    StructInstance toStructInstance(DeviceMessage message);

    DeviceMessage toDeviceMessage(StructInstance structInst);

}
