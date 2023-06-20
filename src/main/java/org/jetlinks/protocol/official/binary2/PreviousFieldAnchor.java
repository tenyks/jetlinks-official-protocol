package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nonnull;

public class PreviousFieldAnchor implements DynamicAnchor {

    @Nonnull
    private FieldDeclaration    targetField;

    private StructInstance      currentStructInst;

    public PreviousFieldAnchor(@Nonnull FieldDeclaration targetField) {
        this.targetField = targetField;
    }

    @Override
    public short getAbsoluteOffset(short relativeOffset) {
        if (currentStructInst == null) {
            throw new IllegalStateException("还没有绑定StructInstance");
        }

        FieldInstance fieldInst = currentStructInst.getFieldInstance(targetField);
        if (fieldInst == null) {
            throw new IllegalStateException(String.format("没有查找到前一字段的实例(%s)", targetField.getCode()));
        }

        if (relativeOffset >= 0) {
            return (short) (fieldInst.getOffset() + fieldInst.getSize() + relativeOffset);
        } else {
            return (short) (fieldInst.getOffset() + relativeOffset);
        }
    }

    @Override
    public void bind(StructInstance structInst) {
        this.currentStructInst = structInst;
    }

}
