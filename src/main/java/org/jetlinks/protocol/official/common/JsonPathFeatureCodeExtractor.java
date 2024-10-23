package org.jetlinks.protocol.official.common;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.databind.JsonNode;
import me.tenyks.utils.JsonUtils;

/**
 * 基于JSON路径抽取的FE
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/23
 * @since V3.1.0
 */
public class JsonPathFeatureCodeExtractor implements FeatureCodeExtractor<JsonNode> {

    private final Expression expr;

    public JsonPathFeatureCodeExtractor(String path) {
        try {
            this.expr = Expression.jsonata(path);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("无效的path表达式(%s)", path), e);
        }
    }

    @Override
    public String extract(JsonNode buf) {
        try {
            return JsonUtils.collectFirst(expr.evaluate(buf));
        } catch (ParseException e) {
            throw new IllegalArgumentException("不适配的表达式", e);
        }
    }

    @Override
    public boolean isValidFeatureCode(String featureCode) {
        return true;
    }
}
