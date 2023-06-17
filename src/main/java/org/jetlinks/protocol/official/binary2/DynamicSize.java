package org.jetlinks.protocol.official.binary2;

public interface DynamicSize {

    short getSize(short mask);

    void bind(StructInstance structInst);

}
