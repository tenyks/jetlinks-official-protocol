package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultFieldDeclaration implements StructFieldDeclaration, Serializable {

    private static final long serialVersionUID = 1540156939056534169L;

    private String              name;

    private String              code;

    private BaseDataType        dataType;

    /**
     * 长度，单位：字节， 空值表示动态长度
     */
    private Short               size;

    private Short               absOffset;

    private DynamicAnchor       refAnchor;

    private short               offsetToAnchor;

    private DynamicSize         refSize;

    private short               sizeMask;

    private boolean             isPayloadField;

    private Object              defaultValue;

    private List<ThingAnnotation> thingAnnotations;

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType) {
        this(name, code, dataType, null);
    }

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType, Short absOffset) {
        this.name = name;
        this.code = code;
        this.dataType = dataType;
        this.size = (dataType.size() >0 ? dataType.size() : null);
        this.absOffset = absOffset;
        this.thingAnnotations = new ArrayList<>();
    }

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType, Short absOffset, Short size) {
        this.name = name;
        this.code = code;
        this.dataType = dataType;
        this.absOffset = absOffset;
        this.size = size;
        this.thingAnnotations = new ArrayList<>();
    }

    /**
     * @param anchor
     * @param offset 如果为正数参考field的结尾;   如果为负数参考field的开头
     * @return
     */
    public DefaultFieldDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        this.refAnchor = anchor;
        this.offsetToAnchor = offset;

        return this;
    }

    public DefaultFieldDeclaration setSizeReference(DynamicSize refSize, short mask) {
        this.refSize = refSize;
        this.sizeMask = mask;

        return this;
    }

    public DefaultFieldDeclaration setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
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

    public BaseDataType  getDataType() {
        return dataType;
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

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    public DefaultFieldDeclaration setPayloadField(boolean payloadField) {
        isPayloadField = payloadField;
        return this;
    }

    public DynamicAnchor    asAnchor() {
        return new PreviousFieldAnchor(this);
    }

    public DynamicSize      asSize() {
        return new PreviousFieldValueAsSize(this);
    }

    @Override
    public Iterable<ThingAnnotation> thingAnnotations() {
        return thingAnnotations;
    }

    public DefaultFieldDeclaration addMeta(ThingAnnotation tAnn) {
        this.thingAnnotations.add(tAnn);
        return this;
    }

    @Override
    public String toString() {
        return "DefaultFieldDeclaration{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", dataType=" + dataType +
                ", size=" + size +
                ", absOffset=" + absOffset +
                ", refAnchor=" + refAnchor +
                ", offsetToAnchor=" + offsetToAnchor +
                ", refSize=" + refSize +
                ", sizeMask=" + sizeMask +
                ", isPayloadField=" + isPayloadField +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
