package org.jetlinks.protocol.common;

import java.util.function.Supplier;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/8/22
 * @since V3.1.0
 */
public abstract class DefaultValueSupplier<T> implements Supplier<T> {

    private final String hint;

    protected DefaultValueSupplier(String hint) {
        this.hint = hint;
    }

    @Override
    public String toString() {
        return "DefaultValueSupplier(" + hint + ')';
    }

    public static <T> Supplier<T> ofStatic(final T defVal) {
        return new DefaultValueSupplier<T>(String.valueOf(defVal)) {
            @Override
            public T get() {
                return defVal;
            }
        };
    }

    public static Supplier<Object> ofUTC() {
        return new DefaultValueSupplier<Object>("$UTC") {
            @Override
            public Integer get() {
                return (int)(System.currentTimeMillis() / 1000);
            }
        };
    }

}
