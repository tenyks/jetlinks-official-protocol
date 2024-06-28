package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

public class DefaultFieldDeclaration implements StructFieldDeclaration, Serializable {

    private static final long serialVersionUID = 1540156939056534169L;

    private BaseDataType        dataType;

    private boolean             isPayloadField;

    private Object              defaultValue;

    private Set<Object>         validValues;

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

    @Nullable
    @Override
    public NRepeatFieldGroupDeclaration includingGroup() {
        return null;
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
