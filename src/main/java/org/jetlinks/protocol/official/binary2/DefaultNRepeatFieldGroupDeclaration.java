package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * @author v-lizy81
 * @date 2024/6/28 22:41
 */
public class DefaultNRepeatFieldGroupDeclaration implements NRepeatFieldGroupDeclaration {

    private String              name;

    private String              code;

    /**
     * 长度，单位：字节， 空值表示动态长度
     */
    private Short               size;

    private StructFieldDeclaration  nReferenceTo;

    private DynamicAnchor       refAnchor;

    private Function<List<FieldInstance>, List<FieldInstance>>  postProcessor;

    @Nullable
    @Override
    public StructFieldDeclaration getNReferenceTo() {
        return nReferenceTo;
    }

    public DefaultNRepeatFieldGroupDeclaration setNReferenceTo(StructFieldDeclaration field) {
        this.nReferenceTo = field;
        return this;
    }

    public DefaultNRepeatFieldGroupDeclaration setNReferenceTo(DynamicAnchor anchor) {
        this.nReferenceTo = field;
        return this;
    }

    @Nonnull
    @Override
    public List<StructFieldDeclaration> getIncludedFields() {
        return null;
    }

    @Nullable
    @Override
    public Function<List<FieldInstance>, List<FieldInstance>> getInstancePostProcessor() {
        return postProcessor;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public DynamicAnchor getDynamicAnchor() {
        return refAnchor;
    }
}
