package org.jetlinks.protocol.official.binary2;

public class PreviousFieldValueAsSize implements DynamicSize {

    public StructFieldDeclaration targetField;

    private StructInstance      currentStructInst;

    public PreviousFieldValueAsSize(StructFieldDeclaration targetField) {
        this.targetField = targetField;
    }

    @Override
    public short getSize(short mask) {
        if (currentStructInst == null) {
            throw new IllegalStateException("还没有绑定StructInstance");
        }

        FieldInstance fieldInst = currentStructInst.getFieldInstance(targetField);
        if (fieldInst == null) {
            throw new IllegalStateException(String.format("没有查找到前面字段的实例(%s)", targetField.getCode()));
        }

        return (short)(fieldInst.getShortValue() & mask);
    }

    @Override
    public void bind(StructInstance structInst) {
        this.currentStructInst = structInst;
    }
}
