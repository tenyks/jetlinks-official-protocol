package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:14
 */
public interface StructReader {

    /**
     * 读取字节流以StructLike方式反序列化
     * @param buf       字节流，（必要）；
     * @return  如果是结构匹配的字节流返回反序列后的实例，否则返回空
     */
    @Nullable
    StructInstance read(ByteBuf buf);

}
