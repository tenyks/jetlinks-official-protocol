package org.jetlinks.protocol.official.binary2;

/**
 * 字段结构声明
 * @author tenyks
 * @since 3.1
 * @version 1.0
 */
public interface StructFieldDeclaration extends StructPartDeclaration {

//    @Nullable
//    NRepeatFieldGroupDeclaration    includingGroup();

    boolean         isPayloadField();

    /**
     * 取值数据类型
     */
    BaseDataType    getDataType();

    Object          getDefaultValue();

}
