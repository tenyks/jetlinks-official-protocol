package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2024/6/28 22:49
 */
public abstract class AbstractStructPartDeclaration implements StructPartDeclaration {

    private final String              name;

    private final String              code;

    /**
     * 长度，单位：字节， 空值表示动态长度
     */
    private final Short               size;

    /**
     * 偏移位置，单位：字节，空值表示仅靠前一字段的结尾
     */
    private final Short                 absOffset;

    private DynamicAnchor               refAnchor;

    private short                       offsetToAnchor;

    private DynamicSize                 refSize;

    private short                       sizeMask;

    private String                      pathInStruct;

    private final List<ThingAnnotation> thingAnnotations;

    /**
     * 数组掩码
     */
    private DataMask                    dataMask;

    protected AbstractStructPartDeclaration(String name, String code, Short absOffset, Short size) {
        this.name = name;
        this.code = code;
        this.absOffset = absOffset;
        this.size = size;
        this.thingAnnotations = new ArrayList<>();
    }

    protected AbstractStructPartDeclaration(String name, String code, String pathInStruct) {
        this.name = name;
        this.code = code;
        this.absOffset = 0;
        this.size = 0;
        this.pathInStruct = pathInStruct;
        this.thingAnnotations = new ArrayList<>();
    }

    /**
     * @param anchor
     * @param offset 如果为正数参考field的结尾;   如果为负数参考field的开头
     * @return
     */
    protected AbstractStructPartDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        this.refAnchor = anchor;
        this.offsetToAnchor = offset;

        return this;
    }

    protected AbstractStructPartDeclaration setSizeReference(DynamicSize refSize, short mask) {
        this.refSize = refSize;
        this.sizeMask = mask;

        return this;
    }

    protected AbstractStructPartDeclaration setDataMask(DataMask mask) {
        this.dataMask = mask;

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

    @Override
    public String getPathInStruct() {
        return pathInStruct;
    }

    public Iterable<ThingAnnotation> thingAnnotations() {
        return thingAnnotations;
    }

    protected AbstractStructPartDeclaration addMeta(ThingAnnotation tAnn) {
        this.thingAnnotations.add(tAnn);

        return this;
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

    public DataMask getDataMask() {
        return dataMask;
    }

    @Override
    public String toString() {
        return "AbstractStructPartDeclaration{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", size=" + size +
                ", absOffset=" + absOffset +
                ", refAnchor=" + refAnchor +
                ", offsetToAnchor=" + offsetToAnchor +
                ", refSize=" + refSize +
                ", sizeMask=" + sizeMask +
                ", thingAnnotations=" + thingAnnotations +
                '}';
    }
}
