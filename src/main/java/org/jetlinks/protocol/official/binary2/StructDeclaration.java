package org.jetlinks.protocol.official.binary2;

public interface StructDeclaration {

    StructDeclaration addField(FieldDeclaration field);

    String  getFeatureCode();

    Iterable<FieldDeclaration>  fields();

}
