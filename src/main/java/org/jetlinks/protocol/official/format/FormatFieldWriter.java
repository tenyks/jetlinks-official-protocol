package org.jetlinks.protocol.official.format;

import org.jetlinks.protocol.official.binary2.FieldInstance;

public interface FormatFieldWriter {

    short write(FieldInstance instance, StringBuilder buf);

}
