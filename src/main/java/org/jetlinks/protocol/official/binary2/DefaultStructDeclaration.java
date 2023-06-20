package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DefaultStructDeclaration implements StructDeclaration {

    @NotNull
    private String      name;

    @NotNull
    private String     featureCode;

    private List<FieldDeclaration> fields;

    public DefaultStructDeclaration(String name, String featureCode) {
        if (name == null || featureCode == null) {
            throw new IllegalArgumentException("参数不全。[0x66DSD1564]");
        }
        this.name = name;
        this.featureCode = featureCode;
        this.fields = new ArrayList<>();
    }

    @Override
    public StructDeclaration addField(FieldDeclaration field) {
        fields.add(field);
        return this;
    }

    @Override
    public @NotNull String getFeatureCode() {
        return featureCode;
    }

    @Override
    public Iterable<FieldDeclaration> fields() {
        return fields;
    }
}
