package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import java.util.List;

/**
 * @author v-lizy81
 * @date 2024/6/28 22:49
 */
public abstract class AbstractStructPartDeclaration implements StructPartDeclaration {

    private String              name;

    private String              code;

    /**
     * 长度，单位：字节， 空值表示动态长度
     */
    private Short               size;

    /**
     * 偏移位置，单位：字节，空值表示仅靠前一字段的结尾
     */
    private Short               absOffset;

    private DynamicAnchor       refAnchor;

    private short               offsetToAnchor;

    private DynamicSize         refSize;

    private short               sizeMask;

    private List<ThingAnnotation> thingAnnotations;

    /**
     * @param anchor
     * @param offset 如果为正数参考field的结尾;   如果为负数参考field的开头
     * @return
     */
    public AbstractStructPartDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        this.refAnchor = anchor;
        this.offsetToAnchor = offset;

        return this;
    }

    @Override
    public short getOffset() {
        if (absOffset != null) return absOffset;

        if (refAnchor == null) {
            throw new IllegalStateException(String.format("字段(%s)声明不完整：缺少absOffset或dynamicOffset", code));
        }

        return refAnchor.getAbsoluteOffset(offsetToAnchor);
    }

    public Iterable<ThingAnnotation> thingAnnotations() {
        return thingAnnotations;
    }

    protected void addMeta(ThingAnnotation tAnn) {
        this.thingAnnotations.add(tAnn);
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
    public short getSize() {
        if (size != null) return size;

        if (refSize == null) {
            throw new IllegalStateException(String.format("字段(%s)声明不完整：缺少size或dynamicSize", code));
        }

        return refSize.getSize(sizeMask);
    }
}
