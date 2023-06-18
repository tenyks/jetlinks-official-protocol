package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

/**
 * @author v-lizy81
 * @date 2023/6/18 22:28
 */
public class DeclarationBasedStructWriter implements StructWriter {

    private StructDeclaration   structDcl;

    public DeclarationBasedStructWriter(StructDeclaration structDcl) {
        this.structDcl = structDcl;
    }

    @Override
    public ByteBuf write(StructInstance instance) {
        return null;
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }
}
