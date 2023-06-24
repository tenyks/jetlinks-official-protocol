package org.jetlinks.protocol.official.binary2;

import java.util.function.Supplier;

public class LazyFieldInstance extends AbstractFieldInstance {

    private Supplier<Object> valueSupplier;

    public LazyFieldInstance(FieldDeclaration fieldDcl, short offset, short size, Supplier<Object> valueSupplier) {
        super(fieldDcl, offset, size);
        this.valueSupplier = valueSupplier;
    }

    @Override
    public Object getValue() {
        return valueSupplier.get();
    }
}
