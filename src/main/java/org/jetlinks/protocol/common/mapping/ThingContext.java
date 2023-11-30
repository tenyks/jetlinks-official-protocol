package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;

public interface ThingContext {

    StructDeclaration getStructDeclaration();

    StructFieldDeclaration getFieldDeclaration();

}
