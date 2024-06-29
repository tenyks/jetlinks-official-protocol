package org.jetlinks.protocol.official.binary2;

/**
 * 动态的重复N次的StructPart
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/29
 * @since V3.1.0
 */
public interface DynamicNRepeat {

    short getNRepeat();

    void bind(StructInstance structInst);

}
