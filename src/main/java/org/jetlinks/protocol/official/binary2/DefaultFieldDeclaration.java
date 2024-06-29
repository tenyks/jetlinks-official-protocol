package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public class DefaultFieldDeclaration extends AbstractStructPartDeclaration
        implements StructFieldDeclaration, Serializable {

    private static final long serialVersionUID = 1540156939056534169L;

    private BaseDataType        dataType;

    private boolean             isPayloadField;

    private Object              defaultValue;

    private Set<Object>         validValues;

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType) {
        this(name, code, dataType, null);
    }

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType, Short absOffset) {
        this(name, code, dataType, absOffset, (dataType.size() >0 ? dataType.size() : null));
    }

    public DefaultFieldDeclaration(String name, String code, BaseDataType dataType, Short absOffset, Short size) {
        super(name, code, absOffset, size);

        this.dataType = dataType;
    }

    public DefaultFieldDeclaration setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public DefaultFieldDeclaration addValidValue(Object... optVals) {
        if (optVals == null || optVals.length == 0) return this;

        if (this.validValues == null) this.validValues = new HashSet<>();
        Collections.addAll(this.validValues, optVals);

        return this;
    }

    public DynamicAnchor    asAnchor() {
        return new PreviousFieldAnchor(this);
    }

    public DynamicSize      asSize() {
        return new PreviousFieldValueAsSize(this);
    }

    public DynamicNRepeat asDynamicNRepeat() {
        return new PreviousFieldValueAsNRepeat(this);
    }

    public BaseDataType  getDataType() {
        return dataType;
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

    @Override
    public DefaultFieldDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        super.setAnchorReference(anchor, offset);
        return this;
    }

    @Override
    public DefaultFieldDeclaration setSizeReference(DynamicSize refSize, short mask) {
        super.setSizeReference(refSize, mask);

        return this;
    }

    @Override
    public DefaultFieldDeclaration addMeta(ThingAnnotation tAnn) {
        super.addMeta(tAnn);

        return this;
    }

    @Override
    public String toString() {
        return "DefaultFieldDeclaration{" +
                "dataType=" + dataType +
                ", isPayloadField=" + isPayloadField +
                ", defaultValue=" + defaultValue +
                ", validValues=" + validValues +
                "} " + super.toString();
    }
}
