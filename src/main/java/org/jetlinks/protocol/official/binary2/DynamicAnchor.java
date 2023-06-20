package org.jetlinks.protocol.official.binary2;

/**
 * 锚点提供
 *
 * @author v-lizy81
 * @date 2023/6/16 21:37
 */
public interface DynamicAnchor {

    short getAbsoluteOffset(short relativeOffset);

    void bind(StructInstance structInst);

}
