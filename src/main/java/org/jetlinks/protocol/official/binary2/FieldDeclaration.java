package org.jetlinks.protocol.official.binary2;

import org.jetlinks.protocol.official.binary.DataType;

/**
 * @author v-lizy81
 * @date 2023/6/12 23:36
 */
public class FieldDeclaration {

    private int         seqNo;

    private String      code;

    private String      name;

    private DataType    dataType;

    private boolean     asType = false;

    private boolean     asMessageId = false;

    private boolean     asHeader = false;
}
