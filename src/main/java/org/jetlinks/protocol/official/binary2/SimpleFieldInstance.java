package org.jetlinks.protocol.official.binary2;

public class SimpleFieldInstance implements FieldInstance {

    private FieldDeclaration   fieldDcl;

    private short   offset;

    private short   size;

    private Object  value;


    public SimpleFieldInstance(FieldDeclaration fieldDcl, short offset, short size, Object value) {
        this.fieldDcl = fieldDcl;
        this.offset = offset;
        this.size = size;
        this.value = value;
    }

    @Override
    public FieldDeclaration getDeclaration() {
        return fieldDcl;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public int getIntValue() {
        return ((Number)value).intValue();
    }

    @Override
    public short getShortValue() {
        return ((Number)value).shortValue();
    }

    @Override
    public String getCode() {
        return fieldDcl.getCode();
    }

    @Override
    public short getOffset() {
        return offset;
    }

    @Override
    public short getSize() {
        return size;
    }
}
