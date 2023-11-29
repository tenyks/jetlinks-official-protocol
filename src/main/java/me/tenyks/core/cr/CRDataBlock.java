package me.tenyks.core.cr;

/**
 * 同类型的行列式数据块，等同与表格
 *
 * @author v-lizy81
 * @date 2023/11/29 23:02
 */
public interface CRDataBlock {

    CRDataBlockMeta         getMeta();

    Iterable<CRDataRow>     getRows();

}
