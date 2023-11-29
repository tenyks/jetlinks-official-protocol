package me.tenyks.core.cr;

import org.jetlinks.protocol.official.binary2.BaseDataType;

/**
 * 列的元信息
 *
 * @author v-lizy81
 * @date 2023/11/29 23:15
 */
public interface ColumnMeta {

    String          code();

    String          name();

    BaseDataType    baseType();

}
