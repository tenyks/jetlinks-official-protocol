package org.jetlinks.protocol.official.binary2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:35
 */
public class RWStructDeclaration  {

    private List<RWFieldDeclaration> fields;

    public RWStructDeclaration() {
        this.fields = new ArrayList<>();
    }

    public String   getFeatureCode() {
        return null;
    }

    public RWStructDeclaration addField(RWFieldDeclaration fieldDcl) {
        this.fields.add(fieldDcl);
        return this;
    }


    public Iterator<RWFieldDeclaration> iterator() {
        return fields.iterator();
    }


}
