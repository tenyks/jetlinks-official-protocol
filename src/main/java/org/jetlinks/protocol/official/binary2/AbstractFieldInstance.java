package org.jetlinks.protocol.official.binary2;

public abstract class AbstractFieldInstance implements FieldInstance {

    private StructFieldDeclaration fieldDcl;

    private Short   offset;

    private Short   size;

    public AbstractFieldInstance(StructFieldDeclaration fieldDcl, Short offset, Short size) {
        this.fieldDcl = fieldDcl;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public StructFieldDeclaration getDeclaration() {
        return fieldDcl;
    }

    @Override
    public int getIntValue() {
        return ((Number)getValue()).intValue();
    }

    @Override
    public short getShortValue() {
        return ((Number)getValue()).shortValue();
    }

    @Override
    public String getCode() {
        return fieldDcl.getCode();
    }

    @Override
    public Short getOffset() {
        return offset;
    }

    @Override
    public Short getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "AbstractFieldInstance{" +
                "fieldDcl=" + fieldDcl +
                ", offset=" + offset +
                ", size=" + size +
                '}';
    }
}
