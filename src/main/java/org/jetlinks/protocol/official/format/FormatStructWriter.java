package org.jetlinks.protocol.official.format;

import org.jetlinks.protocol.official.binary2.StructInstance;

/**
 * 基于文本有格式的结构Writer
 * @author v-lizy81
 * @date 2023/6/16 22:59
 */
public interface FormatStructWriter {

    String write(StructInstance instance);

}
