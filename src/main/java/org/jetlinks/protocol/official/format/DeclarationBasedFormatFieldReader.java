package org.jetlinks.protocol.official.format;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetlinks.protocol.official.binary2.FieldInstance;
import org.jetlinks.protocol.official.binary2.SimpleFieldInstance;
import org.jetlinks.protocol.official.binary2.StructFieldDeclaration;
import org.jetlinks.protocol.official.common.AbstractDeclarationBasedFieldReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

/**
 * @author v-lizy81
 * @version 1.0.0
 * @date 2024/10/19
 * @since V3.1.0
 */
public class DeclarationBasedFormatFieldReader extends AbstractDeclarationBasedFieldReader implements FormatFieldReader {

    private static final Logger log = LoggerFactory.getLogger(DeclarationBasedFormatFieldReader.class);

    private final Expression  expr;

    public DeclarationBasedFormatFieldReader(StructFieldDeclaration fieldDcl) {
        super(fieldDcl);

        try {
            this.expr = Expression.jsonata(fieldDcl.getPathInStruct());
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("字段的引用表达式不合规：%s", fieldDcl.getPathInStruct()));
        }
    }

    @Nullable
    @Override
    public FieldInstance read(JsonNode input) {
        StructFieldDeclaration fDcl = getDeclaration();

        if (input == null) {
            return new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
        }

        JsonNode valNode;
        try {
            valNode = expr.evaluate(input);
        } catch (ParseException e) {
            throw new IllegalArgumentException();
        }
        if (valNode == null) {
            return new SimpleFieldInstance(fDcl, fDcl.getDefaultValue());
        }

        Object val = fDcl.getDataType().fromJson(valNode);

        return new SimpleFieldInstance(fDcl, val);
    }

    @Override
    public String toString() {
        return "DeclarationBasedFormatFieldReader{" +
                "fieldDcl=" + getDeclaration() +
                '}';
    }
}
