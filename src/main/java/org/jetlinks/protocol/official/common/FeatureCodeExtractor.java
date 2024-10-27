package org.jetlinks.protocol.official.common;

/**
 * 消息特征提取，典型用途：二进制报文提取消息报文类型，JSON格式报文提取消息报文类型
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public interface FeatureCodeExtractor<T> {

    String      extract(T buf);

    /**
     *
     * @param featureCode   特征码，（必要）
     * @return  如果是当前协议包含的报文，返回true，否则返回false
     */
    boolean     isValidFeatureCode(String featureCode);
}
