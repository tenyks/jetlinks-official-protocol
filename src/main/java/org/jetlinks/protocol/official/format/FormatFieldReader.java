package org.jetlinks.protocol.official.format;

import com.alibaba.fastjson.JSONObject;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.binary2.StructPartReader;

import javax.annotation.Nullable;

/**
 * 基于文本格式的字段Reader
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/18
 * @since V3.1.0
 */
public interface FormatFieldReader extends StructPartReader {

    @Override
    StructFieldDeclaration getDeclaration();

    /**
     * 从字节流读取字段的取值；
     *
     * @param buf 字节流，（必要）；
     * @return 如果无越界且字节数值匹配字段类型返回字段实例，否则返回空
     */
    @Nullable
    FieldInstance read(JSONObject buf);

}
