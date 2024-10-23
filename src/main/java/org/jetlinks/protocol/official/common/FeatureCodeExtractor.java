package org.jetlinks.protocol.official.common;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public interface FeatureCodeExtractor<T> {

    String      extract(T buf);

    boolean     isValidFeatureCode(String featureCode);
}
