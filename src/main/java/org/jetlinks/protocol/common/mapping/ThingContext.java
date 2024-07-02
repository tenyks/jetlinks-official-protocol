package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;

public interface ThingContext {

    StructDeclaration   getStructDeclaration();

    StructInstance getStructInstance();

}
