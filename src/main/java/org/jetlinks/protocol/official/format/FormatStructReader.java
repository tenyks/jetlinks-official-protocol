package org.jetlinks.protocol.official.format;

import org.jetlinks.protocol.official.binary2.StructInstance;

import javax.annotation.Nullable;

/**
 * 基于文本有格式的结构Reader
 * 
 * @author v-lizy81
 * @date 2023/6/12 23:14
 */
public interface FormatStructReader {

    /**
     * 读取文本字符串以约定格式方式反序列化
     * @param buf       文本字符串，（必要）；
     * @return  如果是结构兼容的格式返回反序列后的实例，否则返回空
     */
    @Nullable
    StructInstance read(String buf);

}
