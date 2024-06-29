package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.common.mapping.ThingAnnotation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author v-lizy81
 * @date 2024/6/28 22:41
 */
public class DefaultNRepeatFieldGroupDeclaration extends AbstractStructPartDeclaration implements NRepeatGroupDeclaration {

    private DynamicNRepeat  nReferenceTo;

    private BiFunction<Integer, List<FieldInstance>, List<FieldInstance>> postProcessor;

    private final List<StructFieldDeclaration>   includedFields;

    public DefaultNRepeatFieldGroupDeclaration(String name, String code, Short absOffset, Short size) {
        super(name, code, absOffset, size);
        this.includedFields = new ArrayList<>();
    }

    public DefaultNRepeatFieldGroupDeclaration  addIncludedField(StructFieldDeclaration fDcl) {
        if (fDcl != null) this.includedFields.add(fDcl);

        return this;
    }

    @Nonnull
    @Override
    public List<StructFieldDeclaration> getIncludedFields() {
        return includedFields;
    }

    @Nullable
    @Override
    public BiFunction<Integer, List<FieldInstance>, List<FieldInstance>> getInstancePostProcessor() {
        return postProcessor;
    }

    public DefaultNRepeatFieldGroupDeclaration
    setInstancePostProcessor(BiFunction<Integer, List<FieldInstance>, List<FieldInstance>> function) {
        this.postProcessor = function;

        return this;
    }

    public DynamicAnchor    asAnchor() {
        return new GroupBeginAnchor(this);
    }

    @Override
    public DynamicNRepeat getDynamicNRepeat() {
        return nReferenceTo;
    }

    public DefaultNRepeatFieldGroupDeclaration setDynamicNRepeat(DynamicNRepeat ref) {
        this.nReferenceTo = ref;
        return this;
    }

    @Override
    public DefaultNRepeatFieldGroupDeclaration setAnchorReference(DynamicAnchor anchor, short offset) {
        return (DefaultNRepeatFieldGroupDeclaration)super.setAnchorReference(anchor, offset);
    }

    @Override
    public DefaultNRepeatFieldGroupDeclaration setSizeReference(DynamicSize refSize, short mask) {
        return (DefaultNRepeatFieldGroupDeclaration)super.setSizeReference(refSize, mask);
    }

    @Override
    public DefaultNRepeatFieldGroupDeclaration addMeta(ThingAnnotation tAnn) {
        return (DefaultNRepeatFieldGroupDeclaration)super.addMeta(tAnn);
    }

    @Override
    public String toString() {
        return "DefaultNRepeatFieldGroupDeclaration{" +
                "nReferenceTo=" + nReferenceTo +
                ", postProcessor=" + postProcessor +
                "} " + super.toString();
    }
}
