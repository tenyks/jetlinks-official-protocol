package org.jetlinks.protocol.official.binary2;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/6/29
 * @since V3.1.0
 */
public interface GroupDeclaration extends StructPartDeclaration {

    @Nonnull
    List<StructFieldDeclaration> getIncludedFields();

}
