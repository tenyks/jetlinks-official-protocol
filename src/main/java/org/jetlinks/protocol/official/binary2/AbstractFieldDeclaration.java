package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.binary.DataType;

public abstract class AbstractFieldDeclaration implements FieldDeclaration {

    private String              code;

    private DataType            dataType;

    /**
     * 长度，单位：字节， 0表示依赖其他字段的值
     */
    private Short               size;

    private Short               absOffset;

    private DynamicAnchor       refAnchor;

    private short               offsetToAnchor;

    private DynamicSize         refSize;

    private short               sizeMask;

    private boolean             isPayloadField;

    protected AbstractFieldDeclaration(String code, DataType dataType, Short absOffset) {
        this.code = code;
        this.dataType = dataType;
        this.absOffset = absOffset;
        this.size = dataType.size();
    }

    protected AbstractFieldDeclaration(String code, DataType dataType, Short absOffset, Short size) {
        this.code = code;
        this.dataType = dataType;
        this.absOffset = absOffset;
        this.size = size;
    }

    /**
     * @param anchor
     * @param offset 如果为正数参考field的结尾;   如果为负数参考field的开头
     * @return
     */
    public AbstractFieldDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        this.refAnchor = anchor;
        this.offsetToAnchor = offset;

        return this;
    }

    public AbstractFieldDeclaration setSizeReference(DynamicSize refSize, short mask) {
        this.refSize = refSize;
        this.sizeMask = mask;

        return this;
    }

    public DataType  getDataType() {
        return dataType;
    }

    @Override
    public short getSize() {
        if (size != null) return size;

        if (refSize == null) {
            throw new IllegalStateException(String.format("字段(%s)声明不完整：缺少size或dynamicSize", code));
        }

        return refSize.getSize(sizeMask);
    }

    @Override
    public short getOffset() {
        if (absOffset != null) return absOffset;

        if (refAnchor == null) {
            throw new IllegalStateException(String.format("字段(%s)声明不完整：缺少absOffset或dynamicOffset", code));
        }

        return refAnchor.getAbsoluteOffset(offsetToAnchor);
    }

    @Override
    public String getCode() {
        return code;
    }

    public DynamicAnchor getDynamicAnchor() {
        return refAnchor;
    }

    public DynamicSize getDynamicSize() {
        return refSize;
    }

    @Override
    public boolean isPayloadField() {
        return isPayloadField;
    }

    public void setPayloadField(boolean payloadField) {
        isPayloadField = payloadField;
    }
}
