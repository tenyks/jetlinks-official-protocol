package org.jetlinks.protocol.official.binary2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleStructInstance implements StructInstance {

    private final StructDeclaration            structDcl;

    private final transient Map<StructFieldDeclaration, List<FieldInstance>>   fieldInstMap;

    private transient Map<String, FieldInstance>   idxByFieldCode;

    private final List<FieldInstance>           fieldInstList;

    public SimpleStructInstance(StructDeclaration dcl) {
        this.structDcl = dcl;
        this.fieldInstMap = new HashMap<>();
        this.fieldInstList = new ArrayList<>();
    }

    @Override
    public FieldInstance getFieldInstance(String fieldCode) {
        if (idxByFieldCode == null) {
            idxByFieldCode = new HashMap<>();

            fieldInstList.forEach((fInst) -> {
                idxByFieldCode.put(fInst.getCode(), fInst);
            });
        }

        return idxByFieldCode.get(fieldCode);
    }

    @Override
    public StructDeclaration getDeclaration() {
        return structDcl;
    }

    @Override
    public Iterable<FieldInstance> filedInstances() {
        return fieldInstList;
    }

    @Override
    public List<FieldInstance>  getFieldInstances(StructFieldDeclaration fieldDcl) {
        return fieldInstMap.get(fieldDcl);
    }

    @Override
    public void addFieldInstance(FieldInstance inst) {
        List<FieldInstance> items = fieldInstMap.computeIfAbsent(inst.getDeclaration(), k -> new ArrayList<>());
        items.add(inst);

        fieldInstList.add(inst);
    }

    @Override
    public void addFieldInstance(String fieldCode, Object value) {
        StructFieldDeclaration fieldDcl = structDcl.getField(fieldCode);
        addFieldInstance(new SimpleFieldInstance(fieldDcl, value));
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(structDcl.getFeatureCode()).append('[');
        boolean isFirst = true;
        for (FieldInstance fInst : fieldInstList) {
            if (!isFirst) buf.append(',');

            buf.append(fInst.getCode()).append('=');
            Object fVal = fInst.getValue();
            buf.append(fVal);

            if (fVal instanceof Byte) {
                buf.append("|0x");
                buf.append(Integer.toHexString((Byte) fVal).toUpperCase());
            } else if (fVal instanceof Short) {
                buf.append("|0x");
                buf.append(Integer.toHexString((Short) fVal).toUpperCase());
            } else if (fVal instanceof Integer) {
                buf.append("|0x");
                buf.append(Integer.toHexString((Integer) fVal).toUpperCase());
            }

            isFirst = false;
        }

        buf.append(']');

        return buf.toString();
    }
}
