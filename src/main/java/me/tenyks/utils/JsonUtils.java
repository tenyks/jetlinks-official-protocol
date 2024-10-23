package me.tenyks.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/9/3
 * @since V3.1.0
 */
public class JsonUtils {

    private static final ObjectWriter WRITER = new ObjectMapper().registerModule(new JavaTimeModule()).writer().with(new MinimalPrettyPrinter());
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String toJsonStr(Object obj) {
        try {
            return WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parse(String jsonStr, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(jsonStr, typeRef);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode parse(String jsonStr) {
        try {
            return MAPPER.readValue(jsonStr, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<String> collectChildren(JsonNode node) {
        if (node == null) return Collections.emptyList();

        List<String> rst = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            JsonNode child = node.get(i);

            rst.add(child.asText());
        }

        return rst;
    }

    public static List<String> collectChildrenIgnoreNull(JsonNode node) {
        if (node == null) return Collections.emptyList();

        List<String> rst = new ArrayList<>();
        for (int i = 0; i < node.size(); i++) {
            JsonNode child = node.get(i);

            if (child instanceof NullNode) continue;

            rst.add(child.asText());
        }

        return rst;
    }

    public static String collectFirst(JsonNode node) {
        if (node == null) return null;

        if (node instanceof ValueNode) {
            return ((ValueNode) node).textValue();
        } else if (node instanceof ArrayNode) {
            ArrayNode arrNode = (ArrayNode) node;
            if (arrNode.isArray()) return null;

            return arrNode.get(0).textValue();
        }

        return node.textValue();
    }

}
