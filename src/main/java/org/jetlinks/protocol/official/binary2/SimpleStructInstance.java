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
    public Iterable<FieldInstance> filedInstances() {
        return fieldInstMap.values();
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

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(structDcl.getFeatureCode()).append('[');
        boolean isFirst = true;
        for (FieldInstance fInst : fieldInstMap.values()) {
            if (!isFirst) buf.append(',');

            buf.append(fInst.getDeclaration().getCode()).append('=');
            if (fInst.getValue() instanceof Byte) {
                buf.append(Integer.toHexString((Byte)fInst.getValue()));
            } else {
                buf.append(fInst.getValue());
            }

            isFirst = false;
        }

        buf.append(']');

        return buf.toString();
    }
}
