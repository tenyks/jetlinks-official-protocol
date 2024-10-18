package org.jetlinks.protocol.official.format;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.protocol.official.binary2.FieldInstance;

public interface FormatFieldWriter {

    short write(FieldInstance instance, JSONObject buf);

}
