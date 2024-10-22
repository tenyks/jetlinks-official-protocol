package org.jetlinks.protocol.official.format;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedFieldWriter;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatFieldWriter extends AbstractDeclarationBasedFieldWriter implements FormatFieldWriter {

    public DeclarationBasedFormatFieldWriter(StructFieldDeclaration fieldDcl) {
        super(fieldDcl);
    }

    @Override
    public short write(FieldInstance instance, JSONObject buf) {
        //TODO 补充实现
        return 0;
    }

}
