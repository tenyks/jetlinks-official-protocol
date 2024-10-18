package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

public interface BinaryFieldReader extends StructPartReader {

    @Override
    StructFieldDeclaration getDeclaration();

    /**
     * 从字节流读取字段的取值；
     *
     * @param buf 字节流，（必要）；
     * @return 如果无越界且字节数值匹配字段类型返回字段实例，否则返回空
     */
    @Nullable
    FieldInstance read(ByteBuf buf);

}
