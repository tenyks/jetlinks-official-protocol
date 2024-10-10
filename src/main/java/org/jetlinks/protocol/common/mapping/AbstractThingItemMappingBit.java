package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.BitDictBook;
import org.jetlinks.protocol.official.common.DictBook;

import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/2
 * @since V3.1.0
 */
public abstract class AbstractThingItemMappingBit<T> implements ThingItemMapping<T> {

    protected StructInstance    structInst;

    private final BitDictBook<T> dictBook;

    public AbstractThingItemMappingBit(BitDictBook<T> dictBook) {
        this.dictBook = dictBook;
    }

    protected List<BitDictBook.Item<T>>   getItem(Object code) {
        return dictBook.decode((byte[])code);
    }

    @Override
    public void bind(StructInstance structInst) {
        this.structInst = structInst;
    }

}
