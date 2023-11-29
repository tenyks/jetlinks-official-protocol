package me.tenyks.core.cr;

/**
 * 行列式数据Scanner
 *
 * @author v-lizy81
 * @date 2023/11/29 23:04
 */
public interface CRDataScanner {

    Iterable<CRDataBlock> readFromJson(String jsonStr);

}
