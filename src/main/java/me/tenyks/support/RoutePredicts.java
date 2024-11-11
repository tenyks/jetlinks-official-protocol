package me.tenyks.support;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetlinks.core.message.DeviceMessage;
import org.jetlinks.core.route.DownstreamRoutePredict;
import org.jetlinks.core.route.ExprEqualDownstreamRoutePredict;
import org.jetlinks.core.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/11/11
 * @since V3.1.0
 */
public class RoutePredicts {

    private static final Logger log = LoggerFactory.getLogger(RoutePredicts.class);

    public static <T extends Route> DownstreamRoutePredict<T, DeviceMessage> ofJsonAndTypeField(String fieldPath, String expectedVal) {
        try {
            final Expression expr = new Expression(fieldPath);

            return new ExprEqualDownstreamRoutePredict<>((msg) -> {
                try {
                    JsonNode rn = expr.evaluate(msg);
                    if (rn == null) return false;

                    return rn.asText().equals(expectedVal);
                } catch (ParseException e) {
                    log.error("[RoutePredict]抽取表达式失败:", e);
                    return false;
                }
            }, expectedVal);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("无效的表达式：%s", fieldPath));
        } catch (IOException e) {
            throw new IllegalStateException("插件初始失败", e);
        }
    }


}
