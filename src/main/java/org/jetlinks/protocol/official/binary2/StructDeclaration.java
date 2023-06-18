package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;

public interface StructDeclaration {

    StructDeclaration addField(FieldDeclaration field);

    @NotNull
    String  getFeatureCode();

    Iterable<FieldDeclaration>  fields();

}
