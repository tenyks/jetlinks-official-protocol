package org.jetlinks.protocol.official.binary2;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.core.message.DeviceMessage;

/**
 * @author v-lizy81
 * @date 2023/6/27 22:28
 */
public class DirectStructAndMessageMapper implements StructAndMessageMapper {

    private StructAndThingMapping   structAndThingMapping;

    private FieldAndPropertyMapping fieldAndPropertyMapping;

    private FieldValueAndPropertyMapping    fieldValueAndPropertyMapping;

    @Override
    public StructInstance toStructInstance(DeviceMessage message) {

        StructDeclaration   structDcl = structAndThingMapping.map(message);
        StructInstance structInst = new SimpleStructInstance(structDcl);

        JSONObject jsonObj = message.toJson();
        for (String pro : jsonObj.keySet()) {
            Object proVal = jsonObj.get(pro);
            FieldDeclaration fieldDcl = fieldAndPropertyMapping.toField(pro);

            Object fieldVal = fieldValueAndPropertyMapping.toFieldValue(fieldDcl, proVal);
            FieldInstance fieldInst = new SimpleFieldInstance(fieldDcl, fieldVal);
            structInst.addFieldInstance(fieldInst);
        }

        return structInst;
    }

    @Override
    public DeviceMessage toDeviceMessage(StructInstance structInst) {
        JSONObject jsonObj = new JSONObject();

        for (FieldInstance fieldInst : structInst.filedInstances()) {
            Object proVal = fieldValueAndPropertyMapping.toPropertyValue(fieldInst);
            String pro = fieldAndPropertyMapping.toProperty(fieldInst.getDeclaration());

            jsonObj.put(pro, proVal);
        }

        DeviceMessage msg = structAndThingMapping.map(structInst.getDeclaration());
        msg.fromJson(jsonObj);
        return msg;
    }

}
