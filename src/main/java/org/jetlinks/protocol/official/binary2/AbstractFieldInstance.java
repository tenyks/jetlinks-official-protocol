package org.jetlinks.protocol.official.binary2;

public abstract class AbstractFieldInstance implements FieldInstance {

    private final StructFieldDeclaration fieldDcl;

    private String  codePrefix;

    private final Short   offset;

    private final Short   size;

    public AbstractFieldInstance(StructFieldDeclaration fieldDcl, Short offset, Short size) {
        this.fieldDcl = fieldDcl;
        this.offset = offset;
        this.size = size;
    }

    public String getCodePrefix() {
        return codePrefix;
    }

    public void setCodePrefix(String codePrefix) {
        this.codePrefix = codePrefix;
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
        return (codePrefix != null ? codePrefix + fieldDcl.getCode() : fieldDcl.getCode());
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
