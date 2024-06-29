package org.jetlinks.protocol.official.binary2;

/**
 * 动态的StructPart长度
 */
public interface DynamicSize {

    short getSize(short mask);

    void bind(StructInstance structInst);

}
