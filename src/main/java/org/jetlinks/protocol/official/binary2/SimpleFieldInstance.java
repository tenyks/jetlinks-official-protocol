package org.jetlinks.protocol.official.binary2;

public class SimpleFieldInstance extends AbstractFieldInstance {

    private Object  value;

    public SimpleFieldInstance(StructFieldDeclaration fieldDcl, Short offset, Short size, Object value) {
        super(fieldDcl, offset, size);
        this.value = value;
    }

    public SimpleFieldInstance(StructFieldDeclaration fieldDcl, Object value) {
        super(fieldDcl, null, null);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

}
