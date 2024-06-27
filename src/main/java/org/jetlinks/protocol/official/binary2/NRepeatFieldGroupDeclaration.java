package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 重复N次（根据报文实际值展开）的字段组
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface NRepeatFieldGroupDeclaration extends StructPartDeclaration {

    /**
     * @return 分组编号，默认从1开始
     */
    default short firstNo() {
        return 1;
    }

    @Nullable
    StructFieldDeclaration          getNReferenceTo();

    List<StructFieldDeclaration>    getIncludedFields();

}
