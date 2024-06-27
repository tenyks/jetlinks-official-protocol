package org.jetlinks.protocol.official.binary2;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/27
 * @since V3.1.0
 */
public interface NRepeatFieldGroupReader extends StructPartReader {

    @Override
    NRepeatFieldGroupDeclaration getDeclaration();

    @Nullable
    List<FieldInstance> read(StructInstance structInst, ByteBuf buf);

    void bind(StructInstance structInst);
}
