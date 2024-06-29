package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 重复N次（根据报文实际值展开）的字段组
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface NRepeatGroupDeclaration extends GroupDeclaration {

    /**
     * 组实例解释后的后处理函数
     */
    @Nullable
    BiFunction<Integer, List<FieldInstance>, List<FieldInstance>> getInstancePostProcessor();

    DynamicNRepeat                  getDynamicNRepeat();
}
