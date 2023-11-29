package org.jetlinks.protocol.official.binary2;

/**
 * 结构字段与物模型属性编码完全相同的Mapping
 */
public class DefaultFieldAndPropertyMapping implements FieldAndPropertyMapping {

    @Override
    public String toProperty(StructFieldDeclaration fieldDcl) {
        return fieldDcl.getCode();
    }

    @Override
    public StructFieldDeclaration toField(StructDeclaration structDcl, String property) {
        return structDcl.getField(property);
    }
}
