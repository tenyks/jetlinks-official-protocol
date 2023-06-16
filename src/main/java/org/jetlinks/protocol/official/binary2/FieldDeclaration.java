package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.binary.DataType;

/**
 * 字段声明：类型、字节数等
 *
 * @author v-lizy81
 * @date 2023/6/12 23:36
 */
public class FieldDeclaration {

    private String      code;

    private DataType    dataType;

    /**
     * 长度，单位：字节， 0表示依赖其他字段的值
     */
    private int         length;

    protected FieldDeclaration() {

    }

    public static FieldDeclaration build(String code, DataType dataType) {
        return null;
    }

    /**
     *
     * @param field
     * @param offset    如果为正数参考field的结尾;   如果为负数参考field的开头
     * @return
     */
    public FieldDeclaration setAnchorReference(FieldDeclaration field, short offset) {
        return this;
    }

    public FieldDeclaration setLengthReference(FieldDeclaration field, short mask) {
        return this;
    }


}
