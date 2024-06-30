package org.jetlinks.protocol.official.binary2;

public class SimpleFieldInstance extends AbstractFieldInstance {

    private final Object  value;

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

    @Override
    public String toString() {
        return String.format("SimpleFieldInstance[code=%s,value=%s,offset=%d,size=%d,dcl=%s]",
                getCode(), getValue(), getOffset(), getSize(), getDeclaration());
    }
}
