package org.jetlinks.protocol.official.binary2;

public class SimpleFieldInstance extends AbstractFieldInstance {

    private Object  value;

    public SimpleFieldInstance(FieldDeclaration fieldDcl, Short offset, Short size, Object value) {
        super(fieldDcl, offset, size);
        this.value = value;
    }

    public SimpleFieldInstance(FieldDeclaration fieldDcl, Object value) {
        super(fieldDcl, null, null);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

}
