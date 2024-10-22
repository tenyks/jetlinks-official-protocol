package org.jetlinks.protocol.official.format;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;

/**
 * 基于文本有格式的结构Writer
 * @author v-lizy81
 * @date 2023/6/16 22:59
 */
public interface FormatStructWriter {

    StructDeclaration getStructDeclaration();

    JsonNode write(StructInstance instance);

    static FormatStructWriter   createInstance(StructDeclaration structDcl) {
        return new DeclarationBasedFormatStructWriter(structDcl);
    }

}
