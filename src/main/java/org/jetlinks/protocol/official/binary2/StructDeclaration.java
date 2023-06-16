package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:35
 */
public class StructDeclaration implements Iterable<FieldDeclaration> {

    private List<FieldDeclaration> fields;

    public StructDeclaration() {
        this.fields = new ArrayList<>();
    }

    public String   getFeatureCode() {
        return null;
    }

    public StructDeclaration addField(FieldDeclaration fieldDcl) {
        this.fields.add(fieldDcl);
        return this;
    }

    @Override
    public Iterator<FieldDeclaration> iterator() {
        return fields.iterator();
    }




}
