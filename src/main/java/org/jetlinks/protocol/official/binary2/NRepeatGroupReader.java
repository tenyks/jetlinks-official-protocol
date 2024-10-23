package org.jetlinks.protocol.official.binary2;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 重复N次的字段组Reader
 *
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface NRepeatGroupReader extends StructPartReader {

    @Override
    NRepeatGroupDeclaration getDeclaration();

    @Nullable
    List<FieldInstance> read(ByteBuf buf);

    @Nullable
    List<FieldInstance> read(JsonNode input);

    void bind(@NotNull StructInstance structInst);
}
