package org.jetlinks.protocol.official.binary2;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 结构体实例
 *
 * @author v-lizy81
 * @date 2023/6/16 21:38
 */
public interface StructInstance {

    @NotNull
    StructDeclaration       getDeclaration();

    Iterable<FieldInstance> filedInstances();

    FieldInstance           getFieldInstance(String fieldCode);

    default FieldInstance   getFieldInstance(StructFieldDeclaration fieldDcl) {
        return getFieldInstance(fieldDcl, 0);
    }

    default FieldInstance   getFieldInstance(StructFieldDeclaration fieldDcl, int idx) {
        List<FieldInstance> items = getFieldInstances(fieldDcl);

        return (items != null && items.size() > idx ? items.get(idx) : null);
    }

    List<FieldInstance>     getFieldInstances(StructFieldDeclaration fieldDcl);

    void addFieldInstance(FieldInstance inst);

    default void addFieldInstance(Iterable<FieldInstance> instances) {
        if (instances == null) return ;

        for (FieldInstance fInst : instances) {
            addFieldInstance(fInst);
        }
    }

    void addFieldInstance(String fieldCode, Object value);

    default String getFieldStringValueWithDef(String fieldCode, String defVal) {
        FieldInstance fInst = getFieldInstance(fieldCode);

        String fVal = fInst != null ? fInst.getStringValue(null) : null;
        if (fVal != null) return fVal;

        StructFieldDeclaration fDcl = getDeclaration().getField(fieldCode);
        if (fDcl != null) {
            Object fValObj = fDcl.getDefaultValue();
            return (fValObj instanceof String ? (String) fValObj : (fValObj != null ? fValObj.toString() : defVal));
        }

        return defVal;
    }
}
