package org.jetlinks.protocol.official.common;

import org.jetlinks.protocol.official.binary2.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public abstract class AbstractDeclarationBasedStructReader {

    private final StructDeclaration structDcl;

    private final List<StructPartReader> partReaders;

    public AbstractDeclarationBasedStructReader(StructDeclaration structDcl) {
        this.structDcl = structDcl;
        this.partReaders = new ArrayList<>();
        for (StructPartDeclaration partDcl : structDcl.parts()) {
            this.partReaders.add(StructPartReader.create(structDcl, partDcl));
        }
    }

    protected StructInstance    createNewStructInstance() {
        return new SimpleStructInstance(structDcl);
    }

    public StructDeclaration getStructDeclaration() {
        return structDcl;
    }

    public List<StructPartReader> getStructPartReaders() {
        return partReaders;
    }
}
