package org.jetlinks.protocol.official.format;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.protocol.official.binary2.FieldInstance;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatFieldWriter implements FormatFieldWriter {

    @Override
    public short write(FieldInstance instance, JSONObject buf) {
        return 0;
    }

}
