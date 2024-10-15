package org.jetlinks.protocol.official.binary2;

import me.tenyks.core.crc.CRCCalculator;
import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultStructDeclaration implements StructDeclaration {

    @NotNull
    private final String      name;

    @NotNull
    private final String      featureCode;

    private final List<ThingAnnotation> thingAnnotations;

    private final List<StructPartDeclaration>   parts;

    private final Map<String, StructPartDeclaration>   idxByCodeMap;

    private MessageIdMappingAnnotation  msgIdMappingAnnotation;

    private CRCCalculator crcCalculator;

    /**
     * 支持编码
     */
    private boolean     enableEncode;

    /**
     * 支持解码
     */
    private boolean     enableDecode;

    private transient String    _serviceIdOrFunctionId;

    public DefaultStructDeclaration(String name, String featureCode) {
        if (name == null || featureCode == null) {
            throw new IllegalArgumentException("参数不全。[0x66DSD1564]");
        }
        this.name = name;
        this.featureCode = featureCode;
        this.parts = new ArrayList<>();
        this.idxByCodeMap = new HashMap<>();
        this.thingAnnotations = new ArrayList<>();
    }

    public StructDeclaration addField(StructFieldDeclaration field) {
        parts.add(field);
        idxByCodeMap.put(field.getCode(), field);
        return this;
    }

    public StructDeclaration addGroup(NRepeatGroupDeclaration fieldGrp) {
        parts.add(fieldGrp);
        return this;
    }

    @Override
    public StructFieldDeclaration getField(String code) {
        StructPartDeclaration part = idxByCodeMap.get(code);

        return (part instanceof StructFieldDeclaration ? (StructFieldDeclaration) part : null);
    }

    @Override
    public NRepeatGroupDeclaration getFieldGroup(String code) {
        StructPartDeclaration part = idxByCodeMap.get(code);

        return (part instanceof NRepeatGroupDeclaration ? (NRepeatGroupDeclaration) part : null);
    }

    @Override
    public StructPartDeclaration getPart(String code) {
        return idxByCodeMap.get(code);
    }

    public DefaultStructDeclaration setCRCCalculator(CRCCalculator crcCal) {
        this.crcCalculator = crcCal;
        return this;
    }

    public DefaultStructDeclaration enableEncode() {
        this.enableEncode = true;
        return this;
    }

    public DefaultStructDeclaration disableEncode() {
        this.enableEncode = false;
        return this;
    }

    public DefaultStructDeclaration enableDecode() {
        this.enableDecode = true;
        return this;
    }

    public DefaultStructDeclaration disableDecode() {
        this.enableDecode = false;
        return this;
    }

    @Override
    public @NotNull String getFeatureCode() {
        return featureCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Iterable<StructPartDeclaration> parts() {
        return parts;
    }

    @Override
    public Iterable<StructFieldDeclaration> fields() {
        List<StructFieldDeclaration> fields = parts.stream().filter((item) -> item instanceof StructFieldDeclaration)
                .map((item) -> (StructFieldDeclaration)item).collect(Collectors.toList());
        return fields;
    }

    @Override
    public CRCCalculator getCRCCalculator() {
        return crcCalculator;
    }

    public boolean isEnableEncode() {
        return enableEncode;
    }

    public boolean isEnableDecode() {
        return enableDecode;
    }

    public DefaultStructDeclaration addThingAnnotation(ThingAnnotation tAnn) {
        this.thingAnnotations.add(tAnn);
        return this;
    }

    public DefaultStructDeclaration addThingAnnotation(Iterable<ThingAnnotation> tAnnIterable) {
        if (tAnnIterable != null) {
            tAnnIterable.forEach(this.thingAnnotations::add);
        }

        return this;
    }

    public String     getServiceIdOrFunctionId() {
        if (_serviceIdOrFunctionId != null) return _serviceIdOrFunctionId;

        for (ThingAnnotation ta : thingAnnotations()) {
            if ("event".equals(ta.getThingKey())) {
                _serviceIdOrFunctionId = ta.getThingValue();
                return _serviceIdOrFunctionId;
            }
            if ("serviceId".equals(ta.getThingKey())) {
                _serviceIdOrFunctionId = ta.getThingValue();
                return _serviceIdOrFunctionId;
            }
        }

        return null;
    }

    @Override
    public Iterable<ThingAnnotation> thingAnnotations() {
        return this.thingAnnotations;
    }

    @Override
    public MessageIdMappingAnnotation messageIdMappingAnnotation() {
        return msgIdMappingAnnotation;
    }

    public DefaultStructDeclaration addMetaAnnotation(MessageIdMappingAnnotation msgIdMappingAnnotation) {
        this.msgIdMappingAnnotation = msgIdMappingAnnotation;
        return this;
    }
}
