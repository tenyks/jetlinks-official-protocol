package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.DictBook;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/2
 * @since V3.1.0
 */
public abstract class AbstractThingItemMapping<T> implements ThingItemMapping<T> {

    protected StructInstance    structInst;

    private final DictBook<?, T> dictBook;

    public AbstractThingItemMapping(DictBook<?, T> dictBook) {
        this.dictBook = dictBook;
    }

    protected DictBook.Item<?, T> getItem(Object code) {
        return dictBook.getOrCreate(code);
    }

    @Override
    public void bind(StructInstance structInst) {
        this.structInst = structInst;
    }

}
