package me.tenyks.core.cr;

/**
 * 数据块元信息
 *
 * @author v-lizy81
 * @date 2023/11/29 23:14
 */
public interface CRDataBlockMeta {

    /**
     * 数据块的名称
     */
    String  getName();

    /**
     * 数据块的编码
     */
    String  getCode();

    /**
     * 数据块的引用路径
     */
    String  getReferencePath();

}
