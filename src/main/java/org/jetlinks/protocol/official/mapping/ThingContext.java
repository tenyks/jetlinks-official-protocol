package org.jetlinks.protocol.official.mapping;

import org.jetlinks.protocol.official.binary2.StructDeclaration;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;

public interface ThingContext {

    StructDeclaration getStructDeclaration();

    StructFieldDeclaration getFieldDeclaration();

}
