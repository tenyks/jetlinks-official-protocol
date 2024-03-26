package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultStructDeclaration implements StructDeclaration {

    @NotNull
    private String      name;

    @NotNull
    private String      featureCode;

    private List<ThingAnnotation> thingAnnotations;

    private List<StructFieldDeclaration> fields;

    private Map<String, StructFieldDeclaration> idxByCodeMap;

    private CRCCalculator       crcCalculator;

    /**
     * 支持编码
     */
    private boolean     enableEncode;

    /**
     * 支持解码
     */
    private boolean     enableDecode;

    public DefaultStructDeclaration(String name, String featureCode) {
        if (name == null || featureCode == null) {
            throw new IllegalArgumentException("参数不全。[0x66DSD1564]");
        }
        this.name = name;
        this.featureCode = featureCode;
        this.fields = new ArrayList<>();
        this.idxByCodeMap = new HashMap<>();
        this.thingAnnotations = new ArrayList<>();
    }

    @Override
    public StructDeclaration addField(StructFieldDeclaration field) {
        fields.add(field);
        idxByCodeMap.put(field.getCode(), field);
        return this;
    }

    @Override
    public StructFieldDeclaration getField(String code) {
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
    public Iterable<StructFieldDeclaration> fields() {
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
            tAnnIterable.forEach(v -> this.thingAnnotations.add(v));
        }

        return this;
    }

    @Override
    public Iterable<ThingAnnotation> thingAnnotations() {
        return this.thingAnnotations;
    }
}
