package org.jetlinks.protocol.official.binary2;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/29
 * @since V3.1.0
 */
public class PreviousFieldValueAsNRepeat implements DynamicNRepeat {

    public StructFieldDeclaration   targetField;

    private StructInstance          currentStructInst;

    private transient Short         _value;

    public PreviousFieldValueAsNRepeat(StructFieldDeclaration targetField) {
        this.targetField = targetField;
    }

    @Override
    public short getNRepeat() {
        if (_value != null) return _value;

        if (currentStructInst == null) {
            throw new IllegalStateException("还没有绑定StructInstance");
        }

        FieldInstance fieldInst = currentStructInst.getFieldInstance(targetField);
        if (fieldInst == null) {
            throw new IllegalStateException(String.format("没有查找到前面字段的实例(%s)", targetField.getCode()));
        }

        _value = fieldInst.getShortValue();
        return _value;
    }

    @Override
    public void bind(StructInstance structInst) {
        this.currentStructInst = structInst;
    }

}
