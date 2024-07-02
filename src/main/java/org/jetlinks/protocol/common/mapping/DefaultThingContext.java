package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/2
 * @since V3.1.0
 */
public class DefaultThingContext implements ThingContext {

    private final StructDeclaration structDcl;

    private final StructInstance    structInst;

    public DefaultThingContext(StructDeclaration structDcl, StructInstance structInst) {
        this.structDcl = structDcl;
        this.structInst = structInst;
    }

    @Override
    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }

    @Override
    public StructInstance getStructInstance() {
        return structInst;
    }
}
