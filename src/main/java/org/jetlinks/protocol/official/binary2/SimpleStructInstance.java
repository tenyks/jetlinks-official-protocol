package org.jetlinks.protocol.official.binary2;

import java.util.HashMap;
import java.util.Map;

public class SimpleStructInstance implements StructInstance {

    private StructDeclaration            structDcl;

    private Map<FieldDeclaration, FieldInstance>   fieldInstMap;

    public SimpleStructInstance(StructDeclaration dcl) {
        this.structDcl = dcl;
        this.fieldInstMap = new HashMap<>();
    }

    @Override
    public StructDeclaration getDeclaration() {
        return structDcl;
    }

    @Override
    public FieldInstance getFieldInstance(FieldDeclaration fieldDcl) {
        return fieldInstMap.get(fieldDcl);
    }

    @Override
    public void addFieldInstance(FieldInstance inst) {
        fieldInstMap.put(inst.getDeclaration(), inst);
    }

    @Override
    public void addFieldInstance(String fieldCode, Object value) {
        FieldDeclaration fieldDcl = structDcl.getField(fieldCode);
        addFieldInstance(new SimpleFieldInstance(fieldDcl, value));
    }
}
