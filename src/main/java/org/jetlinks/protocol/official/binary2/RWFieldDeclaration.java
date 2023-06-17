package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;
import org.jetlinks.protocol.official.binary.DataType;

/**
 * 字段声明：类型、字节数等
 *
 * @author v-lizy81
 * @date 2023/6/12 23:36
 */
public class RWFieldDeclaration extends AbstractFieldDeclaration {

    public RWFieldDeclaration(String code, DataType dataType, Short absOffset) {
        super(code, dataType, absOffset);
    }

    public RWFieldDeclaration(String code, DataType dataType, Short absOffset, Short size) {
        super(code, dataType, absOffset, size);
    }


}
