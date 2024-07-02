package org.jetlinks.protocol.common.mapping;

import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.binary2.StructInstance;
import org.jetlinks.protocol.official.common.DictBook;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/7/2
 * @since V3.1.0
 */
public abstract class AbstractWithPreconditionThingItemMapping<T> extends AbstractThingItemMapping<T> {

    private final StructFieldDeclaration    preconditionFieldDcl;

    private final DictBook<?, T>   preconditionFieldDictBook;

    private transient FieldInstance _preconditionFieldInst;

    public AbstractWithPreconditionThingItemMapping(StructFieldDeclaration preconditionFieldDcl,
                                                    DictBook<?, T> preconditionFieldDictBook,
                                                    DictBook<?, T> dictBook) {
        super(dictBook);

        this.preconditionFieldDcl = preconditionFieldDcl;
        this.preconditionFieldDictBook = preconditionFieldDictBook;
    }

    @Override
    public void bind(StructInstance structInst) {
        super.bind(structInst);
        _preconditionFieldInst = null;
    }

    protected DictBook.Item<?, T> getPreconditionItem() {
        if (_preconditionFieldInst == null) {
            _preconditionFieldInst = (structInst != null ? structInst.getFieldInstance(preconditionFieldDcl) : null);
        }

        return (_preconditionFieldInst != null ? preconditionFieldDictBook.getOrCreate(_preconditionFieldInst.getValue()) : null);
    }
}
